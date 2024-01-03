package me.lotabout.codegenerator;

import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.PackageChooser;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * @Author: zjh
 * @Date: 2023/12/30 22:17
 */
public class TestAction extends AnAction {
    @Override
    public void update(@NotNull AnActionEvent event) {
        // Using the event, evaluate the context,
        // and enable or disable the action.
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
//        JDialog jDialog = new JDialog();
//        PsiFile data = e.getDataContext().getData(CommonDataKeys.PSI_FILE);
//        Module moduleForFile = ModuleUtil.findModuleForFile(data);
//
//        PackageChooserDialog chooserDialog = new PackageChooserDialog("hhh", moduleForFile);
//        chooserDialog.showAndGet();
    }
}
