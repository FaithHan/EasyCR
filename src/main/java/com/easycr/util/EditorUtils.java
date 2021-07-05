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

    public static String getSelectedText(Editor editor) {
        String selectedText = editor.getSelectionModel().getSelectedText();
        String codeDemo = Optional.ofNullable(selectedText).map(String::trim).orElse(null);
        if (StringUtils.isEmpty(codeDemo)) {
            return null;
        }

        List<String> lines = ListUtils.trim(Arrays.asList(selectedText.split(SPLIT_PATTERN)), value -> StringUtils.isEmpty(value.trim()));

        int startNotEmptyLineNumber = getStartNotEmptyLineNumber(editor);
        int lineStartOffset = editor.getDocument().getLineStartOffset(startNotEmptyLineNumber - 1);
        int lineEndOffset = editor.getDocument().getLineEndOffset(startNotEmptyLineNumber - 1);
        String lineContent = editor.getDocument().getText(new TextRange(lineStartOffset, lineEndOffset));
        if (lines.size() != 1) {
            String firstLine = lines.get(0);
            int i = lineContent.indexOf(firstLine);
            lines.set(0, StringUtils.SPACE.repeat(i) + firstLine);
        }
        lines = IndentFourSpace(lines);
        return String.join("\n", lines);
    }

    public static int getColumn(Editor editor) {
        String codeDemo = Optional.ofNullable(editor.getSelectionModel().getSelectedText())
                .map(String::trim).orElse(null);
        if (StringUtils.isEmpty(codeDemo)) {
            return editor.getCaretModel().getCurrentCaret().getLogicalPosition().line + 1;
        } else {
            return getStartNotEmptyLineNumber(editor);
        }
    }

    private static int getStartNotEmptyLineNumber(Editor editor) {
        int beginEmptyLineCount = (int) Arrays.stream(Objects.requireNonNull(editor.getSelectionModel().getSelectedText()).split(SPLIT_PATTERN))
                .takeWhile(line -> line.trim().equals(StringUtils.EMPTY)).count();
        return editor.offsetToLogicalPosition(editor.getSelectionModel().getSelectionStart()).line + 1 + beginEmptyLineCount;
    }

    private static List<String> IndentFourSpace(List<String> lines) {
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
