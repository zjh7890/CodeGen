package me.lotabout.codegenerator.util;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import me.lotabout.codegenerator.CodeGeneratorSettings;

import java.awt.*;

/**
 * @Author: zjh
 * @Date: 2024/1/1 01:34
 */
public class GetProjectUtils {
    public static Project getProject() {
        WindowManager windowManager = WindowManager.getInstance();
        Window activeWindow = windowManager.getMostRecentFocusedWindow();
        // 获取当前 project
        DataContext dataContext = DataManager.getInstance().getDataContext(activeWindow);
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        return project;
    }

    public static CodeGeneratorSettings getSettings() {
        Project project = getProject();
        return project.getService(CodeGeneratorSettings.class);
    }

    public static CodeGeneratorSettings getSettings(Project project) {
        return project.getService(CodeGeneratorSettings.class);
    }

    public static Module getModule(String m, Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            if (module.getName().equals(m)) {
                return module;
            }
        }
        return null;
    }
}
