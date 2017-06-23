package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;

public final class InternalScene {

    private boolean firstDraw = true;
    private StagePair attachedStage;
    private TransformedScene transformedScene;

    public boolean isFirstDraw() {
        return firstDraw;
    }

    public void wasDrawn() {
        firstDraw = false;
    }

    public StagePair getAttachedStage() {
        return attachedStage;
    }

    public void attachStage(StagePair stage) {
        this.attachedStage = stage;
    }

    public TransformedScene getTransformedScene() {
        return transformedScene;
    }

    public void setTransformedScene(TransformedScene transformedScene) {
        this.transformedScene = transformedScene;
    }
}
