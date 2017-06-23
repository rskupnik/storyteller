package com.github.rskupnik.storyteller.core.scenetransform;

import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A complex data class that holds the output of transforming a Scene
 * by {@link SceneTransformer}.
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
public class TransformedScene {

    private List<Pair<Actor, List<Fragment>>> data = new ArrayList<>();

    public List<Pair<Actor, List<Fragment>>> getData() {
        return data;
    }

    public void addActor(Actor actor) {
        addActor(actor, new ArrayList<Fragment>());
    }

    public void addActor(Actor actor, List<Fragment> fragments) {
        data.add(Pair.with(actor, fragments));
    }

    private Pair<Actor, List<Fragment>> findByActor(Actor actor) {
        for (Pair<Actor, List<Fragment>> pair : data) {
            if (pair.getValue0().equals(actor))
                return pair;
        }
        return null;
    }
}
