package com.github.rskupnik.storyteller.wrappers.complex;

import com.github.rskupnik.storyteller.core.transformation.BasicTransformer;

import java.util.ArrayList;

/**
 * A complex data class that holds the output of transforming a Scene
 * by {@link BasicTransformer}.
 * <br/>
 * It holds the following relation: {@code [Actor - [<GlyphLayout, Optional<Rectangle>, Vector2>]]}
 * In a list of actors, for each Actor, we keep a list of triplets which contains: the GlyphLayout, and optional Rectangle
 * if the Actor is clickable, a Vector2 (which is the position).
 * <br/>
 * A more visual representation (JSON):<br/>
 * <pre>
 * {@code [
 *      {
 *          "actor" : Actor_object,
 *          "data" : [
 *              {
 *                  "glyphLayout" : "This contains the text to be drawn in form of a GL",
 *                  "optRectangle" : "Optional Rectangle defining the clickable area if actor is clickable",
 *                  "position" : "A Vector2 containing the position of the actor"
 *              },
 *              ...
 *          ]
 *      },
 *      ...
 * ]}
 * </pre>
 */
public class TransformedScene<T> extends ArrayList<T> {

    /*private List<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>> data = new ArrayList<>();

    public List<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>> getData() {
        return data;
    }

    public void add(Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> pair) {
        data.add(pair);
    }

    public void print() {
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> pair : data) {
            System.out.println("Actor ["+pair.getValue0().getText()+"]");
            for (Triplet<GlyphLayout, Rectangle, Vector2> triplet : pair.getValue1()) {
                System.out.println("    = ["+triplet.getValue2().x+", "+triplet.getValue2().y+"]");
                System.out.println("        = GL: width["+triplet.getValue0().width+"]");
                Rectangle r = triplet.getValue1();
                if (r != null)
                    System.out.println("        = R: pos["+r.x+", "+r.y+"] wh["+r.width+", "+r.height+"]");
            }
        }
    }*/
}
