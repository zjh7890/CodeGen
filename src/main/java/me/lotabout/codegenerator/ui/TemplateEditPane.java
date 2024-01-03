package me.lotabout.codegenerator.ui;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.uiDesigner.core.GridConstraints;
import me.lotabout.codegenerator.CodeGeneratorSettings;
import me.lotabout.codegenerator.config.*;
import me.lotabout.codegenerator.util.GetProjectUtils;
import org.jetbrains.java.generate.config.DuplicationPolicy;
import org.jetbrains.java.generate.config.InsertWhere;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class TemplateEditPane {
    private JPanel templateEdit;
    private JComboBox templateTypeCombo;
    private JTextField templateIdText;
    private JTextField templateNameText;
    private JPanel editorPane;
    private JTextField fileEncodingText;
    private JCheckBox jumpToMethodCheckBox;
    private JRadioButton askRadioButton;
    private JRadioButton replaceExistingRadioButton;
    private JRadioButton generateDuplicateMemberRadioButton;
    private JRadioButton atCaretRadioButton;
    private JRadioButton atEndOfClassRadioButton;
    private JScrollPane settingsPanel;
    private JCheckBox templateEnabledCheckBox;
    private JTabbedPane templateTabbedPane;
    private JButton addMemberButton;
    private JButton addClassButton;
    private JTextField classNameVmText;
    private JCheckBox alwaysPromptForPackageCheckBox;
    private JComboBox<ProjectLayerConfig> projectLayer;
    private JButton addTextInputButton;
    private Editor editor;
    private List<SelectionPane> pipeline = new ArrayList<>();

    String projectLayerName = "";

    public TemplateEditPane(CodeTemplate codeTemplate) {
        settingsPanel.getVerticalScrollBar().setUnitIncrement(16); // scroll speed

        templateIdText.setText(codeTemplate.getId());
        templateNameText.setText(codeTemplate.name);
        templateEnabledCheckBox.setSelected(codeTemplate.enabled);
        fileEncodingText.setText(StringUtil.notNullize(codeTemplate.fileEncoding, CodeTemplate.DEFAULT_ENCODING));
        templateTypeCombo.setSelectedItem(codeTemplate.type);
        jumpToMethodCheckBox.setSelected(codeTemplate.jumpToMethod);
        classNameVmText.setText(codeTemplate.classNameVm);
        this.projectLayerName = codeTemplate.projectLayer;

        CodeGeneratorSettings settings = GetProjectUtils.getSettings();
        List<ProjectLayerConfig> layers = settings.getProjectLayers();
        layers.forEach(x -> projectLayer.addItem(x));
        projectLayer.setSelectedIndex(-1);

        //        projectLayer.
        for (ProjectLayerConfig layer : layers) {
            if (layer.getName().equals(projectLayerName)) {
                projectLayer.setSelectedItem(layer);
            }
        }

        alwaysPromptForPackageCheckBox.setSelected(codeTemplate.alwaysPromptForPackage);

        askRadioButton.setSelected(false);
        replaceExistingRadioButton.setSelected(false);
        generateDuplicateMemberRadioButton.setSelected(false);
        switch (codeTemplate.whenDuplicatesOption) {
            case ASK:
                askRadioButton.setSelected(true);
                break;
            case REPLACE:
                replaceExistingRadioButton.setSelected(true);
                break;
            case DUPLICATE:
                generateDuplicateMemberRadioButton.setSelected(true);
                break;
        }

        atCaretRadioButton.setSelected(false);
        atEndOfClassRadioButton.setSelected(false);
        switch (codeTemplate.insertNewMethodOption) {
            case AT_CARET:
                atCaretRadioButton.setSelected(true);
                break;
            case AT_THE_END_OF_A_CLASS:
                atEndOfClassRadioButton.setSelected(true);
                break;
            default:
                break;
        }

        codeTemplate.pipeline.forEach(this::addMemberSelection);
        addMemberButton.addActionListener(e -> {
            int currentStep = findMaxStepPostfix(pipeline, "member");
            MemberSelectionConfig config = new MemberSelectionConfig();
            config.postfix = String.valueOf(currentStep + 1);
            addMemberSelection(config);
        });
        addClassButton.addActionListener(e -> {
            int currentStep = findMaxStepPostfix(pipeline, "class");
            ClassSelectionConfig config = new ClassSelectionConfig();
            config.postfix = String.valueOf(currentStep + 1);
            addMemberSelection(config);
        });
        addTextInputButton.addActionListener(e -> {
            int currentStep = findMaxStepPostfix(pipeline, "input");
            TextInputConfig config = new TextInputConfig();
            config.postfix = String.valueOf(currentStep + 1);
            addMemberSelection(config);
        });

        addVmEditor(codeTemplate.template);
    }

    private static int findMaxStepPostfix(List<SelectionPane> pipelinePanes, String type) {
        return pipelinePanes.stream()
                .filter(p -> p.type().equals(type))
                .map(SelectionPane::postfix)
                .filter(str -> str.matches("\\d+"))
                .map(Integer::valueOf)
                .max(Comparator.naturalOrder())
                .orElse(0);
    }

    private void addMemberSelection(PipelineStep step) {
        if (step == null) {
            return;
        }

        String title = "";
        if (step instanceof MemberSelectionConfig) {
            title = "Member";
        } else if (step instanceof ClassSelectionConfig) {
            title = "Class";
        } else if (step instanceof  TextInputConfig) {
            title = "Text";
        }

        SelectionPane pane = new SelectionPane(step, this);
        pipeline.add(pane);
        templateTabbedPane.addTab(title, pane.getComponent());
    }

    private void addVmEditor(String template) {
        EditorFactory factory = EditorFactory.getInstance();
        Document velocityTemplate = factory.createDocument(template);
        editor = factory.createEditor(velocityTemplate, null, FileTypeManager.getInstance()
                .getFileTypeByExtension("vm"), false);
        GridConstraints constraints = new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_WEST,
                GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_WANT_GROW,
                GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(0, 0), null, 0, true);

        editorPane.add(editor.getComponent(), constraints);
    }

    public String id() {
        return templateIdText.getText();
    }

    public String name() {
        return templateNameText.getText();
    }

    public boolean enabled() {
        return templateEnabledCheckBox.isSelected();
    }

    public String template() {
        return editor.getDocument().getText();
    }

    public String fileEncoding() {
        return fileEncodingText.getText();
    }

    public String type() {
        return (String) templateTypeCombo.getSelectedItem();
    }

    public boolean jumpToMethod() {
        return jumpToMethodCheckBox.isSelected();
    }

    public DuplicationPolicy duplicationPolicy() {
        if (askRadioButton.isSelected()) {
            return DuplicationPolicy.ASK;
        } else if (replaceExistingRadioButton.isSelected()) {
            return DuplicationPolicy.REPLACE;
        } else if (generateDuplicateMemberRadioButton.isSelected()) {
            return DuplicationPolicy.DUPLICATE;
        }
        return DuplicationPolicy.ASK;
    }

    public InsertWhere insertWhere() {
        if (atCaretRadioButton.isSelected()) {
            return InsertWhere.AT_CARET;
        } else if (atEndOfClassRadioButton.isSelected()) {
            return InsertWhere.AT_THE_END_OF_A_CLASS;
        }
        return InsertWhere.AT_CARET;
    }

    public JPanel templateEdit() {
        return templateEdit;
    }

    public String classNameVm() {
        return classNameVmText.getText();
    }

    public String defaultTargetPackage() {
        return ((ProjectLayerConfig) projectLayer.getSelectedItem()).getPath();
    }

    public String defaultTargetModule() {
        return ((ProjectLayerConfig) projectLayer.getSelectedItem()).getModule();
    }

    public boolean alwaysPromptForPackage() {
        return this.alwaysPromptForPackageCheckBox.isSelected();
    }

    public String toString() {
        return this.name();
    }

    public CodeTemplate getCodeTemplate() {
        CodeTemplate template = new CodeTemplate(this.id());
        template.name = this.name();
        template.type = this.type();
        template.enabled = this.enabled();
        template.fileEncoding = this.fileEncoding();
        template.template = this.template();
        template.jumpToMethod = this.jumpToMethod();
        template.insertNewMethodOption = this.insertWhere();
        template.whenDuplicatesOption = this.duplicationPolicy();
        template.pipeline = pipeline.stream().map(PipelineStepConfig::getConfig).collect(Collectors.toList());
        template.classNameVm = this.classNameVm();
        if (this.projectLayer.getSelectedItem() != null) {
            template.projectLayer = ((ProjectLayerConfig) this.projectLayer.getSelectedItem()).getName();
        }
        template.alwaysPromptForPackage = this.alwaysPromptForPackage();

        return template;
    }

    public void removePipelineStep(PipelineStepConfig stepToRemove) {
        int index = this.pipeline.indexOf(stepToRemove);
        PipelineStepConfig step = this.pipeline.remove(index);
        this.templateTabbedPane.remove(step.getComponent());
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        // 回显
        projectLayer = new ComboBox<>();
    }
}
