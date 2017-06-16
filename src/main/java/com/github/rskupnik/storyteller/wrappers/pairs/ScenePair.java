package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.internals.InternalScene;
import com.github.rskupnik.storyteller.peripheral.Scene;

public final class ScenePair extends PairWrapper<Scene, InternalScene> {

    public ScenePair(Scene left, InternalScene right) {
        super(left, right);
    }

    public Scene scene() {
        return super.left();
    }

    public InternalScene internal() {
        return super.right();
    }

    @Override
    public int hashCode() {
        return left().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Scene)
            return left().equals(obj);
        else if (obj instanceof ScenePair)
            return left().equals(((ScenePair) obj).left());
        else return false;
    }
}
