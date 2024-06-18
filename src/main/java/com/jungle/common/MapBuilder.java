package com.jungle.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

public class MapBuilder<K, V> {
    private final Map<K, V> map;

    public MapBuilder() {
        super();
        this.map = new HashMap<>();
    }

    public MapBuilder(@NotNull Map<K, V> map) {
        super();
        this.map = map;
    }

    @NotNull
    public MapBuilder<K, V> withEntry(K key, V value) {
        map.put(key, value);
        return this;
    }

    @NotNull
    public Map<K, V> build() {
        return Collections.unmodifiableMap(map);
    }
}
