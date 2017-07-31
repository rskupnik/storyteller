package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import static com.github.rskupnik.storyteller.statefulobjects.states.SceneState.ID.*;

public final class SceneState extends State<SceneState.ID> {

    protected enum ID {
        FIRST_DRAW,
        ATTACHED_STAGE,
        TRANSFORMED_SCENE
    }

    public boolean isFirstDraw() {
        return (boolean) get(FIRST_DRAW);
    }

    public void wasDrawn() {
        put(FIRST_DRAW, false);
    }

    public StatefulStage getAttachedStage() {
        return (StatefulStage) get(ATTACHED_STAGE);
    }

    public void attachStage(StatefulStage stage) {
        put(ATTACHED_STAGE, stage);
    }

    public TransformedScene getTransformedScene() {
        return (TransformedScene) get(TRANSFORMED_SCENE);
    }

    public void setTransformedScene(TransformedScene scene) {
        put(TRANSFORMED_SCENE, scene);
    }
}
