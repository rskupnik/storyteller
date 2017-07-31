package com.github.rskupnik.storyteller.structs;

import java.util.HashMap;

public abstract class Vault<E extends Enum, T> extends HashMap<E, T> {

    public Vault with(E id, T item) {
        put(id, item);
        return this;
    }

    public T get(E key) {
        return super.get(key);
    }

    public T put(E key, T val) {
        return super.put(key, val);
    }
}
