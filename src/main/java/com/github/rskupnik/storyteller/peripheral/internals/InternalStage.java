package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

public final class InternalStage {

    private ScenePair attachedScene;
    private RenderingUnit renderingUnit;

    public ScenePair getAttachedScene() {
        return attachedScene;
    }

    public void attachScene(ScenePair scene) {
        this.attachedScene = scene;
    }

    public RenderingUnit getRenderingUnit() {
        return renderingUnit;
    }

    public void setRenderingUnit(RenderingUnit renderingUnit) {
        this.renderingUnit = renderingUnit;
    }
}
