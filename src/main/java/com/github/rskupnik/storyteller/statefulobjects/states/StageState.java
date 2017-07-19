package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.core.renderingunits.background.BackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

public final class StageState extends State {

    private static final String ATTACHED_SCENE = "attachedScene";
    private static final String RENDERING_UNIT = "renderingUnit";
    private static final String BACKGROUND_RENDERING_UNIT = "backgroundRenderingUnit";

    public void attachScene(StatefulScene scene) {
        put(ATTACHED_SCENE, scene);
    }

    public StatefulScene getAttachedScene() {
        return (StatefulScene) get(ATTACHED_SCENE);
    }

    public void setRenderingUnit(RenderingUnit renderingUnit) {
        put(RENDERING_UNIT, renderingUnit);
    }

    public RenderingUnit getRenderingUnit() {
        return (RenderingUnit) get(RENDERING_UNIT);
    }

    public void setBackgroundRenderingUnit(BackgroundRenderingUnit renderingUnit) {
        put(BACKGROUND_RENDERING_UNIT, renderingUnit);
    }

    public BackgroundRenderingUnit getBackgroundRenderingUnit() {
        return (BackgroundRenderingUnit) get(BACKGROUND_RENDERING_UNIT);
    }
}
