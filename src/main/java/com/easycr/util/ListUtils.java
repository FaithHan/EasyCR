package com.easycr.util;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ListUtils {

    public static <T> List<T> trim(List<T> list, Predicate<? super T> predicate) {
        list = list.stream()
                .dropWhile(predicate)
                .collect(Collectors.toList());

        Collections.reverse(list);

        list = list.stream()
                .dropWhile(predicate)
                .collect(Collectors.toList());

        Collections.reverse(list);
        return list;

    }

}
