package com.github.rskupnik.storyteller.core.scenetransform;

import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.structs.Fragment;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

/**
 * A complex data class that holds the output of transforming a Scene
 * by {@link SceneTransformer}.
 * <br/>
 * It holds the following relation: {@code [StatefulActor - [<GlyphLayout, Optional<Rectangle>, Vector2>]]}
 * In a list of StatefulActors, for each StatefulActor, we keep a list of triplets which contains: the GlyphLayout, and optional Rectangle
 * if the StatefulActor is clickable, a Vector2 (which is the position).
 * <br/>
 * A more visual representation (JSON):<br/>
 * <pre>
 * {@code [
 *      {
 *          "StatefulActor" : StatefulActor_object,
 *          "data" : [
 *              {
 *                  "glyphLayout" : "This contains the text to be drawn in form of a GL",
 *                  "optRectangle" : "Optional Rectangle defining the clickable area if StatefulActor is clickable",
 *                  "position" : "A Vector2 containing the position of the StatefulActor"
 *              },
 *              ...
 *          ]
 *      },
 *      ...
 * ]}
 * </pre>
 */
public class TransformedScene {

    private List<Pair<StatefulActor, List<Fragment>>> data = new ArrayList<>();

    // TODO: Extract these to a SavedState class or sth
    private Vector2 savedCursorPosition;
    private Boolean savedIsFirstLine;

    public List<Pair<StatefulActor, List<Fragment>>> getData() {
        return data;
    }

    public void addActor(StatefulActor actor) {
        addActor(actor, new ArrayList<Fragment>());
    }

    public void addActor(StatefulActor actor, List<Fragment> fragments) {
        data.add(Pair.with(actor, fragments));
    }

    private Pair<StatefulActor, List<Fragment>> findByActor(StatefulActor actor) {
        for (Pair<StatefulActor, List<Fragment>> pair : data) {
            if (pair.getValue0().equals(actor))
                return pair;
        }
        return null;
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
