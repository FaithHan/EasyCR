package com.easycr.dialog;

import com.easycr.util.MembersUtils;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;

public class RecordDialog extends DialogWrapper {

    public final JTextField message = new JTextField();
    public final ComboBox<String> member = new ComboBox<>();


    public RecordDialog() {
        super(true); // use current window as parent
        setTitle("EasyCR Mark");
        init();
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        JPanel centerPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Message"), message, 1, false)
                .addLabeledComponent(new JBLabel("Member"), member, 1, false)
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
        MembersUtils.getCRMembers().forEach(member::addItem);
        centerPanel.setPreferredSize(new Dimension(400, 0));
        return centerPanel;
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return message;
    }
}
