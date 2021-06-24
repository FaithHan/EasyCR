package com.easycr.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DayResult {

    private String date;

    private Map<String, List<FixItem>> projectResultMap = new LinkedHashMap<>();

    public DayResult(String date) {
        this.date = date;
    }
}
