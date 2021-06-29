package com.easycr.util;

public abstract class StringUtils {

    public static final String EMPTY  = "";

    public static boolean isEmpty(String string) {
        return string == null || string.isEmpty();
    }

    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }
}
