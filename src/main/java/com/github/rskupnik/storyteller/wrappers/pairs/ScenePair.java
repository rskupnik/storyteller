package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.internals.InternalScene;
import com.github.rskupnik.storyteller.peripheral.Scene;

/**
 * This class represents a design pattern I came up with to separate peripheral class from internal state.
 *
 * The left side of the pair is a Peripheral class that is supposed to be seen and used by an external user.
 * The right side of the pair is an Internal class which represents some kind of state that the engine needs
 * to keep to operate on the object but it should not be visible to the external user.
 *
 * The way these kinds of classes are meant to be used is to use the XPair in the scope of the engine but every
 * method that returns to the external user of the engine should unwrap the pair and return the left side.
 */
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
