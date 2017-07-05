package com.github.rskupnik.storyteller.structs;

import java.util.HashMap;

public abstract class DataBank<T> extends HashMap<String, T> {

    public DataBank with(String id, T item) {
        put(id, item);
        return this;
    }
}
