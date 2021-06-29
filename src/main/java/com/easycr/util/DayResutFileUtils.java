package com.easycr.util;


import com.easycr.entity.DayResult;
import com.easycr.entity.FixItem;
import com.easycr.setting.AppSettingsState;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import lombok.SneakyThrows;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class DayResutFileUtils {

    private static final String FILE_NAME = "EasyCR.md";

    @SneakyThrows
    public static Map<String, DayResult> converter() {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        VirtualFile fileByIoFile = Optional.ofNullable(LocalFileSystem.getInstance().findFileByIoFile(new File(service.logPath)))
                .orElseThrow(RuntimeException::new);

        VirtualFile fileChild = fileByIoFile.findChild(FILE_NAME);

        if (fileChild == null || !fileChild.exists()) {
            WriteCommandAction.runWriteCommandAction(null, () -> {
                try {
                    fileByIoFile.createChildData(null, FILE_NAME);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        fileChild = Optional.ofNullable(fileByIoFile.findChild(FILE_NAME)).orElseThrow(RuntimeException::new);


        BufferedReader reader = new BufferedReader(new InputStreamReader(fileChild.getInputStream(), StandardCharsets.UTF_8));
        Map<String, DayResult> map = new LinkedHashMap<>();
        DayResult currentDayResult = null;
        String currentProjectName = null;
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("## Date")) {
                String date = line.substring(9);
                currentDayResult = map.computeIfAbsent(date, DayResult::new);
            }
            if (line.startsWith("####")) {
                currentProjectName = line.substring(5);
                Objects.requireNonNull(currentDayResult);
                currentDayResult.getProjectResultMap().computeIfAbsent(currentProjectName, key -> new ArrayList<>());
            }
            if (line.startsWith("*")) {
                int a = line.indexOf("/");
                int b = line.indexOf(":");
                while (line.charAt(b) != ' ') {
                    b++;
                }
                int c = line.lastIndexOf("@");
                FixItem fixItem = FixItem.builder()
                        .position(line.substring(a, b))
                        .message(line.substring(b, c).trim())
                        .member(line.substring(c + 1))
                        .build();
                Objects.requireNonNull(currentDayResult);
                currentDayResult.getProjectResultMap().get(currentProjectName).add(fixItem);
            }
        }
        return map;
    }

    @SneakyThrows
    public static void print(Map<String, DayResult> map) {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        VirtualFile fileByIoFile = Optional.ofNullable(LocalFileSystem.getInstance().findFileByIoFile(new File(service.logPath, FILE_NAME)))
                .orElseThrow(RuntimeException::new);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
        map.values().forEach(dayResult -> {
                    String date = dayResult.getDate();
                    printWriter.println(String.format("## Date: %s", date));
                    dayResult.getProjectResultMap().forEach((project, fixItems) -> {
                        printWriter.println();
                        printWriter.println(String.format("#### %s", project));
                        printWriter.println();
                        for (FixItem fixItem : fixItems) {
                            printWriter.println(fixItem);
                        }
                    });
                    printWriter.println();
                    printWriter.println("---");
                    printWriter.println();
                });
        printWriter.flush();
        fileByIoFile.setBinaryContent(out.toByteArray());
    }


}
