package me.lotabout.codegenerator.ui;

import com.intellij.openapi.ui.DialogWrapper;
import me.lotabout.codegenerator.config.PipelineStep;
import me.lotabout.codegenerator.config.TextInputConfig;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;
import static javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE;

public class GetInputPane extends DialogWrapper implements PipelineStepConfig {
    private final TextInputConfig config;
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    public JTextField textField1;


    public GetInputPane(TextInputConfig config) {
        super(true);
        this.config = config;

        textField1 = new JTextField();
        Dimension dimension = new Dimension(150, 30);
        textField1.setPreferredSize(dimension);
        contentPane.add(textField1);

        buttonOK = new JButton();
        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });
        contentPane.add(buttonOK);
        init();
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }


    @Override
    public PipelineStep getConfig() {
        return config;
    }

    @Override
    public JComponent getComponent() {
        return contentPane;
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return contentPane;
    }
}
