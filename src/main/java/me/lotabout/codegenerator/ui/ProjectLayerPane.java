package me.lotabout.codegenerator.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import me.lotabout.codegenerator.CodeGeneratorSettings;
import me.lotabout.codegenerator.model.LayerTable;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.awt.*;

/**
 * @Author: zjh
 * @Date: 2023/12/31 17:06
 */
@XmlRootElement(name = "projectLayer")
@XmlAccessorType(XmlAccessType.FIELD)
public class ProjectLayerPane {
    private JPanel topPanel;
    private JButton button1;
    private JButton button2;
    private JButton duplicateButton;
    private JButton upButton;
    private JButton downButton;

    private JPanel layerListPanel;

    public LayerTable layerList;

    ProjectLayerPane(CodeGeneratorSettings settings) {
        layerList = new LayerTable(settings.getProjectLayers());
        layerListPanel.add(
                ToolbarDecorator.createDecorator(layerList)
                        .setAddAction(button -> layerList.addLayer())
                        .setRemoveAction(button ->
                                layerList.removeSelectedLayeres())
                        .setEditAction(button ->
                                layerList.editLayer())
                        .setMoveUpAction(anActionButton ->
                                layerList.moveUp())
                        .setMoveDownAction(anActionButton ->
                                layerList.moveDown())
                        .createPanel(), BorderLayout.CENTER);
    }

    private void createUIComponents() {

    }

    public JPanel getPane() {
        return topPanel;
    }
}
