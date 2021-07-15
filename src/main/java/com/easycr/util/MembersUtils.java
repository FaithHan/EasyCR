package com.easycr.util;

import com.easycr.setting.AppSettingsState;
import com.intellij.openapi.application.ApplicationManager;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class MembersUtils {

    public static List<String> getCRMembers() {
        AppSettingsState service = ApplicationManager.getApplication().getService(AppSettingsState.class);
        List<String> members = Arrays.stream(service.members.split(","))
                .map(String::trim)
                .filter(StringUtils::isNotEmpty)
                .collect(Collectors.toList());
        if (members.size() <= 1) {
            return Collections.emptyList();
        }
        String first = members.remove(0);
        members.sort(Comparator.comparing(Function.identity()));
        members.add(0, first);
        return members;
    }
}
