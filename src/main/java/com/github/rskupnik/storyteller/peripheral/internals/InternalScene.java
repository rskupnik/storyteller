package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StatefulStage;

public final class InternalScene {

    private boolean firstDraw = true;
    private StatefulStage attachedStage;
    private TransformedScene transformedScene;

    public boolean isFirstDraw() {
        return firstDraw;
    }

    public void wasDrawn() {
        firstDraw = false;
    }

    public StatefulStage getAttachedStage() {
        return attachedStage;
    }

    public void attachStage(StatefulStage stage) {
        this.attachedStage = stage;
    }

    public TransformedScene getTransformedScene() {
        return transformedScene;
    }

    public void setTransformedScene(TransformedScene transformedScene) {
        this.transformedScene = transformedScene;
    }
}
