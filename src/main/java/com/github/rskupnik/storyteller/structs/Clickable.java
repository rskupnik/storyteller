package com.github.rskupnik.storyteller.structs;

import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

public final class Clickable extends Tuple {

    public Clickable(Rectangle rectangle, Actor actor, State state) {
        super(rectangle, actor, state);
    }

    public Clickable(Rectangle rectangle, Actor actor) {
        this(rectangle, actor, new State());
    }

    @Override
    public int getSize() {
        return 3;
    }

    public Rectangle rectangle() {
        return (Rectangle) getValue(0);
    }

    public Actor actor() {
        return (Actor) getValue(1);
    }

    public State state() {
        return (State) getValue(2);
    }
}
