package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * Temporary placeholder class
 */
public class UndefinedOutput {

    private List<Pair<Actor, ArrayList<Pair<Pair<GlyphLayout, Rectangle>, Vector2>>>> data = new ArrayList<>();

    public void add(Pair<Actor, ArrayList<Pair<Pair<GlyphLayout, Rectangle>, Vector2>>> pair) {
        data.add(pair);
    }

    public void print() {
        for (Pair<Actor, ArrayList<Pair<Pair<GlyphLayout, Rectangle>, Vector2>>> pair : data) {
            System.out.println("Actor ["+pair.getValue0().getText()+"]");
            for (Pair<Pair<GlyphLayout, Rectangle>, Vector2> pair1 : pair.getValue1()) {
                System.out.println("    = ["+pair1.getValue1().x+", "+pair1.getValue1().y+"]");
                System.out.println("        = GL: width["+pair1.getValue0().getValue0().width+"]");
                Rectangle r = pair1.getValue0().getValue1();
                if (r != null)
                    System.out.println("        = R: pos["+r.x+", "+r.y+"] wh["+r.width+", "+r.height+"]");
            }
        }
    }
}
