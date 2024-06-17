package com.jungle.common;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

public class StringUtils {
    @NotNull
    public static String unescapeString(@NotNull String s) {
        return StringEscapeUtils.unescapeJava(s);
    }
}
