package com.github.rskupnik.storyteller.core.scenetransform;

import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.structs.Fragment;
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

    // TODO: Extract these to a SavedState class or sth
    private Vector2 savedCursorPosition;
    private Boolean savedIsFirstLine;

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

    public void merge(TransformedScene scene) {
        for (Pair<Actor, List<Fragment>> pair : scene.getData()) {
            data.add(pair);
        }
    }

    public Vector2 getSavedCursorPosition() {
        return savedCursorPosition;
    }

    public void setSavedCursorPosition(Vector2 savedCursorPosition) {
        this.savedCursorPosition = savedCursorPosition;
    }

    public Boolean getSavedIsFirstLine() {
        return savedIsFirstLine;
    }

    public void setSavedIsFirstLine(boolean savedIsFirstLine) {
        this.savedIsFirstLine = savedIsFirstLine;
    }
}
