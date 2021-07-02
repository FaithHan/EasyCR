package com.easycr.util;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.TextRange;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.IntStream;

public class EditorUtils {

    public static String getSelectedText(Editor editor) {
        String selectedText = editor.getSelectionModel().getSelectedText();
        String codeDemo = Optional.ofNullable(selectedText).map(String::trim).orElse(null);
        if (StringUtils.isEmpty(codeDemo)) {
            return null;
        }

        List<String> lines = ListUtils.trim(Arrays.asList(selectedText.split("\n")), value -> StringUtils.isEmpty(value.trim()));

        int startNotEmptyLineNumber = getStartNotEmptyLineNumber(editor);
        int lineStartOffset = editor.getDocument().getLineStartOffset(startNotEmptyLineNumber - 1);
        int lineEndOffset = editor.getDocument().getLineEndOffset(startNotEmptyLineNumber - 1);
        String lineContent = editor.getDocument().getText(new TextRange(lineStartOffset, lineEndOffset));
        if (lines.size() == 1) {
            return "    " + lines.get(0).trim();
        } else {
            String firstLine = lines.get(0);
            int i = lineContent.indexOf(firstLine);
            StringBuilder sb = new StringBuilder();
            IntStream.range(0, i).forEach(value -> sb.append(" "));
            sb.append(firstLine);
            lines.set(0, sb.toString());
            return String.join("\n", lines);
        }

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
        int beginEmptyLineCount = (int) Arrays.stream(Objects.requireNonNull(editor.getSelectionModel().getSelectedText()).split("\n"))
                .takeWhile(line -> line.trim().equals(StringUtils.EMPTY)).count();
        return editor.offsetToLogicalPosition(editor.getSelectionModel().getSelectionStart()).line + 1 + beginEmptyLineCount;
    }
}
