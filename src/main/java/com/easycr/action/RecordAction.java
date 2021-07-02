package com.easycr.action;

import com.easycr.dialog.RecordDialog;
import com.easycr.entity.DayResult;
import com.easycr.entity.FixItem;
import com.easycr.notify.RecordNotifier;
import com.easycr.setting.AppSettingsState;
import com.easycr.util.DateUtils;
import com.easycr.util.DayResutFileUtils;
import com.easycr.util.EditorUtils;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
        String codeDemo = EditorUtils.getSelectedText(editor);
        int column = EditorUtils.getColumn(editor);
        Project project = e.getRequiredData(CommonDataKeys.PROJECT);
        String basePath = Optional.ofNullable(project.getBasePath()).orElseThrow(RuntimeException::new);
        String projectName = project.getName();
        String filePath = Optional.of(e.getRequiredData(PlatformDataKeys.FILE_EDITOR))
                .map(FileEditor::getFile)
                .map(VirtualFile::getPath)
                .orElseThrow(RuntimeException::new);

        RecordDialog recordDialog = new RecordDialog();
        recordDialog.setResizable(true);
        if (!recordDialog.showAndGet()) {
            return;
        }

        String message = recordDialog.message.getText();
        String member = Optional.ofNullable(recordDialog.member.getSelectedItem()).map(Object::toString).orElse("");
        String position = filePath.substring(basePath.length()) + ":" + column;

        FixItem fixItem = FixItem.builder()
                .position(position)
                .message(message)
                .codeDemo(codeDemo)
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
