package com.easycr.entity;

import com.easycr.util.StringUtils;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import static com.easycr.constants.Constants.FILE_SEPARATOR;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixItem implements Comparable<FixItem> {

    private String position;
    private String message;
    private String codeDemo;
    private String member;
    private Boolean done;

    @Override
    public String toString() {
        String descriptionLine = "* [ ] " + position + " " + message + " @" + member;
        if (StringUtils.isEmpty(codeDemo)) {
            return descriptionLine;
        }
        return String.join(FILE_SEPARATOR, descriptionLine, "```", codeDemo, "```");
    }

    @Override
    public int compareTo(@NotNull FixItem o) {
        if (member.isEmpty() && o.member.isEmpty()) {
            return 0;
        } else if (member.isEmpty()) {
            return 1;
        } else if (o.member.isEmpty()) {
            return -1;
        } else {
            return member.compareTo(o.member);
        }
    }
}
