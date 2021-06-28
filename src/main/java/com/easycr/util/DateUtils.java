package com.easycr.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {

    private static final String FORMAT = "yyyy-MM-dd";

    public static String formatDate(Date date) {
        return new SimpleDateFormat(FORMAT).format(date);
    }
}
