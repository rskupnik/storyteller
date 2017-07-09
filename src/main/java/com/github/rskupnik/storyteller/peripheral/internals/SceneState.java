package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.wrappers.pairs.StatefulStage;

public final class SceneState extends State {

    private static final String FIRST_DRAW = "firstDraw";
    private static final String ATTACHED_STAGE = "attachedStage";
    private static final String TRANSFORMED_SCENE = "transformedScene";

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
