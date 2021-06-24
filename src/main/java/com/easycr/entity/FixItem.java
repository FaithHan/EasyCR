package com.easycr.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FixItem {

    private String position;
    private String message;
    private String author;
    private Boolean done;

}
