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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class DayResultFileUtils {

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
        boolean codeDemoBegin = false;
        List<String> codeDemoLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            String originLine = line;
            line = line.trim();
            if (PatternUtils.isDate(line)) {
                String date = PatternUtils.getDate(line);
                currentDayResult = map.computeIfAbsent(date, DayResult::new);
            } else if (PatternUtils.isProject(line)) {
                currentProjectName = PatternUtils.getProject(line);
                Objects.requireNonNull(currentDayResult);
                currentDayResult.getProjectResultMap().computeIfAbsent(currentProjectName, key -> new ArrayList<>());
            } else if (PatternUtils.isMessage(line)) {
                FixItem fixItem = FixItem.builder()
                        .position(PatternUtils.getPosition(line))
                        .message(PatternUtils.getMessage(line))
                        .member(PatternUtils.getMember(line))
                        .build();
                Objects.requireNonNull(currentDayResult);
                currentDayResult.getProjectResultMap().get(currentProjectName).add(fixItem);
            } else if (PatternUtils.isCodeBountry(line)) {
                if (codeDemoBegin) {
                    Objects.requireNonNull(currentDayResult);
                    List<FixItem> fixItems = currentDayResult.getProjectResultMap().get(currentProjectName);
                    fixItems.get(fixItems.size() - 1).setCodeDemo(String.join("\n", codeDemoLines));
                    codeDemoLines.clear();
                }
                codeDemoBegin = !codeDemoBegin;
            } else if (!PatternUtils.isCodeBountry(line) && codeDemoBegin) {
                codeDemoLines.add(originLine);
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

    private static class PatternUtils {

        public static final Pattern MESSAGE = Pattern.compile("^\\* \\[ ]\\s+(.*:\\d+)\\s+(.*?)\\s+@(.*)$");

        public static final Pattern DATE = Pattern.compile("^## Date: (\\d{4}-\\d{2}-\\d{2})$");

        public static final Pattern PROJECT = Pattern.compile("^####\\s+(.+)$");

        public static final Pattern CODE_BOUNDRY = Pattern.compile("^```$");

        public static boolean isMessage(String line) {
            return MESSAGE.matcher(line).matches();
        }

        public static boolean isDate(String line) {
            return DATE.matcher(line).matches();
        }

        public static boolean isProject(String line) {
            return PROJECT.matcher(line).matches();
        }

        public static boolean isCodeBountry(String line) {
            return CODE_BOUNDRY.matcher(line).matches();
        }

        public static String getDate(String line) {
            Matcher matcher = DATE.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return null;
            }
        }

        public static String getProject(String line) {
            Matcher matcher = PROJECT.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return null;
            }
        }

        public static String getPosition(String line) {
            Matcher matcher = MESSAGE.matcher(line);
            if (matcher.find()) {
                return matcher.group(1);
            } else {
                return null;
            }
        }

        public static String getMessage(String line) {
            Matcher matcher = MESSAGE.matcher(line);
            if (matcher.find()) {
                return matcher.group(2);
            } else {
                return null;
            }
        }

        public static String getMember(String line) {
            Matcher matcher = MESSAGE.matcher(line);
            if (matcher.find()) {
                return matcher.group(3);
            } else {
                return null;
            }
        }

    }

}
