package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

public final class InternalStage {

    private ScenePair attachedScene;

    public ScenePair getAttachedScene() {
        return attachedScene;
    }

    public void attachScene(ScenePair scene) {
        this.attachedScene = scene;
    }
}
