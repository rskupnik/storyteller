package com.github.rskupnik.storyteller.structs;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Triplet;
import org.javatuples.Tuple;

public final class Clickable extends Tuple {

    public Clickable(Rectangle rectangle, Actor actor, GlyphLayout GL, State state) {
        super(rectangle, actor, GL, state);
    }

    public Clickable(Rectangle rectangle, Actor actor, GlyphLayout GL) {
        this(rectangle, actor, GL, new State());
    }

    @Override
    public int getSize() {
        return 4;
    }

    public Rectangle rectangle() {
        return (Rectangle) getValue(0);
    }

    public Actor actor() {
        return (Actor) getValue(1);
    }

    public GlyphLayout glyphLayout() {
        return (GlyphLayout) getValue(2);
    }

    public State state() {
        return (State) getValue(3);
    }
}
