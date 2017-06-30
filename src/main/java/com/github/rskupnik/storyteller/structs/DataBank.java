package com.github.rskupnik.storyteller.structs;

import java.util.HashMap;

public abstract class DataBank extends HashMap<String, Object> {

    public DataBank with(String id, Object item) {
        put(id, item);
        return this;
    }
}
