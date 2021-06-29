package com.easycr.entity;

import com.easycr.util.StringUtils;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixItem {

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
        return String.join("\n", descriptionLine, "```", codeDemo, "```");
    }
}
