// Copyright 2000-2021 JetBrains s.r.o. and other contributors. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.

package com.easycr.setting;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "org.intellij.sdk.settings.AppSettingsState",
        storages = {@Storage("SdkSettingsPlugin.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState> {

    public static final String DEFAULT_LOG_PATH = "";
    public static final String DEFAULT_MEMBERS = "";

    public String logPath = DEFAULT_LOG_PATH;
    public String members = DEFAULT_MEMBERS;

    public static AppSettingsState getInstance() {
        return ApplicationManager.getApplication().getService(AppSettingsState.class);
    }

    @Nullable
    @Override
    public AppSettingsState getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }

    public boolean check() {
        if ("".equals(logPath)) {
            return false;
        }
        VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(logPath));
        return fileByIoFile != null && fileByIoFile.isDirectory();
    }

}
