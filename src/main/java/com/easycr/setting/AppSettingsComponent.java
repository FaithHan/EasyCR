// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.easycr.setting;

import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBTextField;
import com.intellij.util.ui.FormBuilder;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    private final JBTextField logPath = new JBTextField();
    private final JBTextField members = new JBTextField();

    public AppSettingsComponent() {
        myMainPanel = FormBuilder.createFormBuilder()
                .addLabeledComponent(new JBLabel("Your log path"), logPath, 1, false)
                .addLabeledComponent(new JBLabel("Members"), members, 1, false)
                .addComponent(new JBLabel("Members use \",\"to split"))
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return logPath;
    }

    @NotNull
    public String getLogPath() {
        return logPath.getText();
    }

    public void setLogPath(@NotNull String newText) {
        logPath.setText(newText);
    }

    public String getMembers() {
        return members.getText();
    }

    public void setMembers(String newText) {
        members.setText(newText);
    }

}
