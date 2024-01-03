package me.lotabout.codegenerator.action;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Caret;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import me.lotabout.codegenerator.CodeGeneratorSettings;
import me.lotabout.codegenerator.config.CodeTemplate;
import me.lotabout.codegenerator.worker.JavaCaretWorker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class CodeGeneratorGroup extends ActionGroup implements DumbAware {

    private static final Logger logger = Logger.getInstance(CodeGeneratorGroup.class);

    public CodeGeneratorGroup() {
    }

    @NotNull
    @Override
    public AnAction @NotNull [] getChildren(@Nullable AnActionEvent anActionEvent) {
        if (anActionEvent == null) {
            return AnAction.EMPTY_ARRAY;
        }

        Project project = PlatformDataKeys.PROJECT.getData(anActionEvent.getDataContext());
        if (project == null) {
            return AnAction.EMPTY_ARRAY;
        }

        PsiFile file = anActionEvent.getDataContext().getData(CommonDataKeys.PSI_FILE);
        if (file == null) {
            logger.info("没找到 PsiFile");
            return AnAction.EMPTY_ARRAY;
        }

        Caret caret = anActionEvent.getDataContext().getData(LangDataKeys.CARET);
        boolean isProjectView = caret == null;

        if (!isProjectView) {
            // EditorPopup menu
            PsiElement element = file.findElementAt(caret.getOffset());
            PsiClass clazz = PsiTreeUtil.getParentOfType(element, PsiClass.class, false);
            if (clazz == null) {
                // not inside a class
                return AnAction.EMPTY_ARRAY;
            }
        }


        String fileName = file.getName();



        CodeGeneratorSettings settings = project.getService(CodeGeneratorSettings.class);


        final List<AnAction> children = settings.getCodeTemplates().stream()
                .filter(t -> !isProjectView || (t.type.equals("class") && isProjectView))
                .filter(t -> t.enabled && fileName.matches(t.fileNamePattern))
                .map(CodeGeneratorGroup::getOrCreateAction)
                .collect(Collectors.toList());

        return children.toArray(new AnAction[children.size()]);
    }

    private static AnAction getOrCreateAction(CodeTemplate template) {
        final String actionId = "CodeMaker.Menu.Action." + template.getId();
        AnAction action = ActionManager.getInstance().getAction(actionId);
        if (action == null) {
            action = new CodeGeneratorAction(template.getId(), template.name);
            ActionManager.getInstance().registerAction(actionId, action);
        }
        return action;
    }
}
