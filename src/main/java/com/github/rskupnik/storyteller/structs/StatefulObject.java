package com.github.rskupnik.storyteller.structs;

/**
 * StatefulObjects hold an immutable Object along with a separate State
 * attached to that object.
 */
public abstract class StatefulObject<O extends Object, S extends State> {

    private final O object;
    private final S state;

    public StatefulObject(O object, S state) {
        this.object = object;
        this.state = state;
    }

    public O obj() {
        return object;
    }

    public S state() {
        return state;
    }

    public static boolean isNull(StatefulObject obj) {
        return obj == null || obj.obj() == null || obj.state() == null;
    }

    @Override
    public int hashCode() {
        return obj().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass().equals(this.getClass()))
            return obj().equals(obj);
        else return false;
    }
}
