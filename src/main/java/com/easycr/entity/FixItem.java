package com.easycr.entity;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FixItem {

    private String position;
    private String message;
    private String member;
    private Boolean done;

    @Override
    public String toString() {
        return "* [ ] " + position + " " + message + " @" + member;
    }
}
