package com.easycr.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class EditorUtils {

    private static final String SPLIT_PATTERN = "\r?\n";

    public static String getCodeDemo(Editor editor) {
        if (isSelectedTextBlank(editor)) {
            return null;
        }
        List<String> lines = ListUtils.trim(getSelectedTextLines(editor), String::isBlank);
        formatFirstLine(editor, lines);
        return String.join("\n", indentFourSpace(lines));
    }

    public static int getColumn(Editor editor) {
        if (isSelectedTextBlank(editor)) {
            return editor.getCaretModel().getCurrentCaret().getLogicalPosition().line + 1;
        } else {
            return getStartNotEmptyLineNumber(editor);
        }
    }

    private static boolean isSelectedTextBlank(Editor editor) {
        return Optional.ofNullable(editor.getSelectionModel().getSelectedText())
                .orElse(StringUtils.EMPTY)
                .isBlank();
    }

    private static void formatFirstLine(Editor editor, List<String> lines) {
        int startNotEmptyLineNumber = getStartNotEmptyLineNumber(editor);
        int lineStartOffset = editor.getDocument().getLineStartOffset(startNotEmptyLineNumber - 1);
        int lineEndOffset = editor.getDocument().getLineEndOffset(startNotEmptyLineNumber - 1);
        String lineContent = editor.getDocument().getText(new TextRange(lineStartOffset, lineEndOffset));
        String firstLine = lines.get(0);
        int i = lineContent.indexOf(firstLine);
        lines.set(0, StringUtils.SPACE.repeat(i) + firstLine);
    }

    private static int getStartNotEmptyLineNumber(Editor editor) {
        int beginEmptyLineCount = (int) getSelectedTextLines(editor).stream().takeWhile(String::isBlank).count();
        return editor.offsetToLogicalPosition(editor.getSelectionModel().getSelectionStart()).line + 1 + beginEmptyLineCount;
    }

    private static List<String> getSelectedTextLines(Editor editor) {
        return Optional.ofNullable(editor.getSelectionModel().getSelectedText())
                .map(text -> Arrays.asList(text.split(SPLIT_PATTERN)))
                .orElseGet(Collections::emptyList);
    }

    private static List<String> indentFourSpace(List<String> lines) {
        Pattern pattern = Pattern.compile("(\\s*)(.*)");
        int indentCount = lines.stream()
                .filter(line -> !line.isBlank())
                .map(line -> {
                    Matcher matcher = pattern.matcher(line);
                    if (matcher.find()) {
                        return matcher.group(1);
                    } else {
                        return "";
                    }
                }).map(String::length)
                .min(Comparator.comparing(Function.identity()))
                .map(minSpace -> minSpace - 4)
                .orElse(0);

        if (indentCount == 0) {
            return new ArrayList<>(lines);
        } else {
            return lines.stream().map(line -> {
                if (line.isBlank()) {
                    return line;
                }
                Matcher matcher = pattern.matcher(line);
                int spaceCount;
                if (matcher.find()) {
                    spaceCount = matcher.group(1).length();
                } else {
                    spaceCount = 0;
                }
                spaceCount = spaceCount - indentCount;
                return StringUtils.SPACE.repeat(spaceCount) + matcher.group(2);
            }).collect(Collectors.toList());
        }

    }
}
