package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;

public final class InternalScene {

    private boolean firstDraw = true;
    private StagePair attachedStage;

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
}
