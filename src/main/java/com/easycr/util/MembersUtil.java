package com.easycr.util;

import com.easycr.setting.AppSettingsState;
import com.intellij.openapi.application.ApplicationManager;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MembersUtil {

    public static List<String> getMembers() {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        String members = service.members;
        List<String> collect = Arrays.stream(members.trim().split(","))
                .map(String::trim)
                .filter(member -> member.length() != 0)
                .collect(Collectors.toList());
        String first = collect.remove(0);
        collect.sort(Comparator.comparing(Function.identity()));
        collect.add(0,first);
        return collect;
    }
}
