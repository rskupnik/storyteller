package com.github.rskupnik.storyteller.peripheral.internals;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

public final class StageState extends State {

    private static final String ATTACHED_SCENE = "attachedScene";
    private static final String RENDERING_UNIT = "renderingUnit";

    public void attachScene(ScenePair scene) {
        put(ATTACHED_SCENE, scene);
    }

    public ScenePair getAttachedScene() {
        return (ScenePair) get(ATTACHED_SCENE);
    }

    public void setRenderingUnit(RenderingUnit renderingUnit) {
        put(RENDERING_UNIT, renderingUnit);
    }

    public RenderingUnit getRenderingUnit() {
        return (RenderingUnit) get(RENDERING_UNIT);
    }
}
