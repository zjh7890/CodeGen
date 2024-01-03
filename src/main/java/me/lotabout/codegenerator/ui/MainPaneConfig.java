package me.lotabout.codegenerator.ui;

import me.lotabout.codegenerator.ui.include.IncludeConfig;

import javax.swing.*;

public class MainPaneConfig {

    private JPanel mainPanel;
    private JTabbedPane tabbedPane;

    public MainPaneConfig(ProjectLayerPane projectLayerPane, CodeGeneratorConfig codeGeneratorConfig, IncludeConfig includeConfig) {
        tabbedPane.add("Project Layer", projectLayerPane.getPane());
        tabbedPane.add("Code Templates", codeGeneratorConfig.getMainPane());
        tabbedPane.add("Includes", includeConfig.getMainPane());
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }
}
