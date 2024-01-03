package me.lotabout.codegenerator.ui;

import com.intellij.ide.DataManager;
import com.intellij.ide.util.PackageChooserDialog;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.psi.PsiPackage;
import com.intellij.ui.DocumentAdapter;
import me.lotabout.codegenerator.config.ProjectLayerConfig;
import me.lotabout.codegenerator.model.LayerTable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import java.awt.*;

/**
 * @Author: zjh
 * @Date: 2023/12/31 21:04
 */
public class LayerEditor extends DialogWrapper {
    private final LayerTable.EditValidator editValidator;
    private JPanel panel1;
    private JTextField textField1;

    ComboBox<Module> comboBox;

    private JTextField packageField;

    public LayerEditor(LayerTable.EditValidator editValidator, ProjectLayerConfig projectLayerConfig) {
        super(true);
        setTitle("编辑创建层级");
        this.editValidator = editValidator;
        this.textField1 = new JTextField();
        textField1.setPreferredSize(new Dimension(200, 30));
        this.packageField = new JTextField();
        packageField.setPreferredSize(new Dimension(200, 30));
        textField1.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                updateControls();
            }
        });
        packageField.getDocument().addDocumentListener(new DocumentAdapter() {
            @Override
            public void textChanged(@NotNull DocumentEvent event) {
                updateControls();
            }
        });


        JLabel label = new JLabel();
        label.setText("Name");
        panel1.add(label);

        panel1.add(textField1);

        JLabel label2 = new JLabel();
        label2.setText("Module");
        panel1.add(label2);


        WindowManager windowManager = WindowManager.getInstance();
        Window activeWindow = windowManager.getMostRecentFocusedWindow();
        // 获取当前 project
        DataContext dataContext = DataManager.getInstance().getDataContext(activeWindow);
        Project project = dataContext.getData(CommonDataKeys.PROJECT);
        assert project != null;
        Module[] modules = ModuleManager.getInstance(project).getModules();
        comboBox = new ComboBox<>(modules);
        panel1.add(comboBox);

        JLabel label3 = new JLabel();
        label3.setText("Package");
        panel1.add(label3);
        panel1.add(packageField);

        JButton jButton = new JButton();
        jButton.setText("...");
        jButton.addActionListener(e -> {
            Module module = (Module) comboBox.getSelectedItem();
            PackageChooserDialog chooserDialog = new PackageChooserDialog("hhh", module);
            chooserDialog.selectPackage(packageField.getText());
            boolean b = chooserDialog.showAndGet();
            PsiPackage selectedPackage = chooserDialog.getSelectedPackage();
            packageField.setText(selectedPackage.getQualifiedName());
        });
        panel1.add(jButton);

        if (projectLayerConfig != null) {
            // 回显
            textField1.setText(projectLayerConfig.getName());
            int itemCount = comboBox.getItemCount();
            for (int i = 0; i < itemCount; i++) {
                Module itemAt = comboBox.getItemAt(i);
                if (itemAt.getName().equals(projectLayerConfig.getModule())) {
                    comboBox.setSelectedIndex(i);
                    break;
                }
            }
            packageField.setText(projectLayerConfig.getPath());
        }
        init();
    }

    private void updateControls() {
        getOKAction().setEnabled(editValidator.isOK(textField1.getText(), packageField.getText()));
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return panel1;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public ProjectLayerConfig getLayer() {
        ProjectLayerConfig projectLayerConfig = new ProjectLayerConfig();
        projectLayerConfig.setName(textField1.getText());
        projectLayerConfig.setModule(((Module) comboBox.getSelectedItem()).getName());
        projectLayerConfig.setPath(packageField.getText());
        return projectLayerConfig;
    }
}
