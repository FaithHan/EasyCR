// Copyright 2000-2020 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.easycr.setting;

import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.easycr.setting.AppSettingsState.DEFAULT_LOG_PATH;
import static com.easycr.setting.AppSettingsState.DEFAULT_MEMBERS;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "SDK: Application Settings Example";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = AppSettingsState.getInstance();
        return !mySettingsComponent.getLogPath().equals(settings.logPath) ||
                !mySettingsComponent.getMembers().equals(settings.members);
    }

    @Override
    public void apply() {
        AppSettingsState settings = AppSettingsState.getInstance();
        settings.logPath = mySettingsComponent.getLogPath();
        settings.members = mySettingsComponent.getMembers();
    }

    @Override
    public void reset() {
        mySettingsComponent.setLogPath(DEFAULT_LOG_PATH);
        mySettingsComponent.setMembers(DEFAULT_MEMBERS);
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}
