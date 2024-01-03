package me.lotabout.codegenerator.worker;

import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.codeStyle.JavaCodeStyleManager;
import com.intellij.psi.impl.PsiManagerImpl;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.impl.file.PsiDirectoryImpl;
import me.lotabout.codegenerator.CodeGeneratorSettings;
import me.lotabout.codegenerator.config.CodeTemplate;
import me.lotabout.codegenerator.config.ProjectLayerConfig;
import me.lotabout.codegenerator.config.include.Include;
import me.lotabout.codegenerator.util.GenerationUtil;
import me.lotabout.codegenerator.util.GetProjectUtils;
import me.lotabout.codegenerator.util.PackageUtil;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class JavaClassWorker {
    private static final Logger logger = Logger.getInstance(JavaClassWorker.class);

    public static void execute(@NotNull CodeTemplate codeTemplate, @NotNull List<Include> includes, @NotNull PsiJavaFile selectedFile, @NotNull
            Map<String, Object> context) {
        try {
            final Project project = selectedFile.getProject();

            // fetch necessary parameters

            final String className;
            final String packageName;
            final String FQClass = GenerationUtil.velocityEvaluate(project, context, context, codeTemplate.classNameVm, includes);
            if (logger.isDebugEnabled()) logger.debug("FQClass generated\n" + FQClass);

            int index = FQClass.lastIndexOf(".");
            if (index >= 0) {
                packageName = FQClass.substring(0, index);
                className = FQClass.substring(index + 1);
            } else {
                packageName = "";
                className = FQClass;
            }

            context.put("PackageName", packageName);
            context.put("ClassName", className);

            // generate the content of the class

            final String content = GenerationUtil.velocityEvaluate(project, context, null, codeTemplate.template, includes);
            if (logger.isDebugEnabled()) logger.debug("Method body generated from Velocity:\n" + content);

            final String selectedPackage = selectedFile.getPackageName();
            final String targetPackageName = packageName.equals("") ? selectedPackage : packageName;


            CodeGeneratorSettings settings = GetProjectUtils.getSettings(project);
            ProjectLayerConfig projectLayer = settings.getProjectLayer(codeTemplate.projectLayer);

            // select or create the target package
            final Module currentModule = GetProjectUtils.getModule(projectLayer.getModule(), project);
            assert currentModule != null;

            final VirtualFile moduleRoot = ProjectRootManager.getInstance(project).getFileIndex().getSourceRootForFile(selectedFile.getVirtualFile());
//            assert moduleRoot != null;

            final VirtualFileManager manager = VirtualFileManager.getInstance();

            PsiDirectory targetPackageDir = null;

            Optional<ProjectLayerConfig> first = GetProjectUtils.getSettings().getProjectLayers().stream().filter(x -> x.getName().equals(codeTemplate.projectLayer)).findFirst();

            targetPackageDir = PackageUtil.findSourceDirectoryByModuleName(project, first.get().getModule());

            if (targetPackageDir == null) {
                // package is not found or created.
                return;
            }

            PsiDirectory subdirectory = targetPackageDir;
//            String[] split = ;
//            for (String s : split) {
//                if (StringUtils.isNotBlank(s)) {
//                    PsiDirectory tmp = subdirectory.findSubdirectory(s);
//                    if (tmp == null) {
//                        subdirectory = subdirectory.createSubdirectory(s);
//                    } else {
//                        subdirectory = tmp;
//                    }
//                }
//            }

            final String targetFileName = className + ".java";
            final String targetPath;
            final String targetDirectory;

            String subDirectoryPath = first.get().getModule().replace(".", File.separator);
            targetDirectory = targetPackageDir.getVirtualFile().getPath()
                    + File.separator + StringUtils.join(projectLayer.getPath().split("\\."),  File.separator);
            targetPath = targetDirectory + File.separator + targetFileName;

            final VirtualFile targetFileVf = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(targetPath));
            if (targetFileVf != null && targetFileVf.exists() && !userConfirmedOverride()) {
                return;
            }

            final VirtualFile targetDirectoryVf = manager.refreshAndFindFileByUrl(VfsUtil.pathToUrl(targetDirectory));
            final PsiDirectory targetPsiDirectory = new PsiDirectoryImpl(new PsiManagerImpl(project), targetDirectoryVf);

            final PsiFile targetFile = PsiFileFactory.getInstance(project).createFileFromText(className + ".java", JavaFileType.INSTANCE, content);
            JavaCodeStyleManager.getInstance(project).shortenClassReferences(targetFile);
            CodeStyleManager.getInstance(project).reformat(targetFile);

            WriteCommandAction.runWriteCommandAction(project, () -> {
                try {
                    final PsiFile oldFile = targetPsiDirectory.findFile(targetFileName);
                    if (oldFile != null) {
                        oldFile.delete();
                    }

                    targetPsiDirectory.add(targetFile);
                    PsiFile addedFile = targetPsiDirectory.findFile(targetFile.getName());

                    // open the file in editor
                    ApplicationManager.getApplication()
                            .invokeLater(() -> FileEditorManager.getInstance(project).openFile(addedFile.getVirtualFile(), true, true));
                } catch (Exception e) {
                    e.printStackTrace();
                    GenerationUtil.handleException(project, e);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static boolean userConfirmedOverride() {
        return Messages.showYesNoDialog("Overwrite?", "File Exists", null) == Messages.OK;
    }
}
