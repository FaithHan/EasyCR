package com.easycr.action;

import com.easycr.dialog.RecordDialog;
import com.easycr.entity.DayResult;
import com.easycr.entity.FixItem;
import com.easycr.notify.RecordNotifier;
import com.easycr.setting.AppSettingsState;
import com.easycr.util.DateUtils;
import com.easycr.util.DayResutFileUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RecordAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        if (!service.check()) {
            Messages.showErrorDialog("Before use EasyCR, Your need to config a log path directory to save your CR log", "EasyCR");
            return;
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            Messages.showErrorDialog("You need to select a code line", "EasyCR");
            return;
        }
        int column = editor.getCaretModel().getCurrentCaret().getLogicalPosition().line + 1;
        Project project = e.getProject();
        String basePath = project.getBasePath();
        String projectName = project.getName();
        String filePath = e.getData(PlatformDataKeys.FILE_EDITOR).getFile().getPath();

        RecordDialog recordDialog = new RecordDialog();
        recordDialog.setResizable(true);
        boolean b = recordDialog.showAndGet();
        if (!b) {
            return;
        }

        String message = recordDialog.message.getText();
        String member = recordDialog.member.getSelectedItem().toString();
        String position = filePath.substring(basePath.length()) + ":" + column;

        FixItem fixItem = FixItem.builder()
                .position(position)
                .message(message)
                .member(member)
                .build();

        Map<String, DayResult> dayResultMap = DayResutFileUtils.converter();

        dayResultMap.computeIfAbsent(DateUtils.formatDate(new Date()), DayResult::new)
                .getProjectResultMap().computeIfAbsent(projectName, key -> new ArrayList<>())
                .add(fixItem);

        WriteCommandAction.runWriteCommandAction(null, () -> DayResutFileUtils.print(dayResultMap));
        RecordNotifier.notifyInfo(project, position);
    }


}
