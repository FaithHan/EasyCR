package com.easycr.action;

import com.easycr.entity.DayResult;
import com.easycr.entity.FixItem;
import com.easycr.notify.RecordNotifier;
import com.easycr.setting.AppSettingsState;
import com.easycr.util.DayResutFileUtil;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class RecordAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        if (!service.check()) {
            Messages.showErrorDialog("Before use EasyCR, Your need to config a log path directory to save your CR log", "Hi!");
            return;
        }

        Editor editor = e.getData(CommonDataKeys.EDITOR);
        if (editor == null) {
            Messages.showErrorDialog("You need to select a code line", "Hi!");
            return;
        }
        int column = editor.getCaretModel().getCurrentCaret().getLogicalPosition().line;
        Project project = e.getProject();
        String basePath = project.getBasePath();
        String filePath = e.getData(PlatformDataKeys.FILE_EDITOR).getFile().getPath();
        String projectName = project.getName();

        RecordDialog recordDialog = new RecordDialog();
        recordDialog.setResizable(true);
        boolean b = recordDialog.showAndGet();
        if (!b) {
            return;
        }

        String message;
        String member;
        message = recordDialog.message.getText();
        member = recordDialog.member.getSelectedItem().toString();


        String position = filePath.substring(basePath.length()) + ":" + column;

        Map<String, DayResult> dayResultMap = DayResutFileUtil.converter();

        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());

        FixItem fixItem = new FixItem();
        fixItem.setPosition(position);
        fixItem.setMessage(message);
        fixItem.setAuthor(member);

        dayResultMap.computeIfAbsent(date, key -> new DayResult(date))
                .getProjectResultMap().computeIfAbsent(projectName, key -> new ArrayList<>())
                .add(fixItem);
        WriteCommandAction.runWriteCommandAction(null, () -> DayResutFileUtil.print(dayResultMap));
        RecordNotifier.notifyInfo(null,"EasyCR add a new Item Success!");
    }


}
