package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary placeholder class
 */
public class UndefinedOutput {

    private List<Pair<Actor, ArrayList<Pair<GlyphLayout, Rectangle>>>> data = new ArrayList<>();

    public void add(Pair<Actor, ArrayList<Pair<GlyphLayout, Rectangle>>> pair) {
        data.add(pair);
    }
}
