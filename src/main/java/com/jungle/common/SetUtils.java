package com.jungle.common;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SetUtils {
    @SafeVarargs
    public static final <T> Set<T> newSet(T... elements) {
        Set<T> set = new HashSet<T>();
        Collections.addAll(set, elements);
        return set;
    }
}
