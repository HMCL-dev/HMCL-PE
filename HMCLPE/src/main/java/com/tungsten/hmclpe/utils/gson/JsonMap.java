package com.tungsten.hmclpe.utils.gson;

import java.util.HashMap;
import java.util.Map;

public class JsonMap<K, V> extends HashMap<K, V> {
    public JsonMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    public JsonMap(int initialCapacity) {
        super(initialCapacity);
    }

    public JsonMap() {
        super();
    }

    public JsonMap(Map<? extends K, ? extends V> m) {
        super(m);
    }

    @Override
    public V put(K key, V value) {
        if (key == null) return null;
        return super.put(key, value);
    }
}
