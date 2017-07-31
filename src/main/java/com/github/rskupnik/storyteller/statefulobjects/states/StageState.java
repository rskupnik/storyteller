package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.core.renderingunits.background.BackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

import static com.github.rskupnik.storyteller.statefulobjects.states.StageState.ID.*;

public final class StageState extends State<StageState.ID> {

    protected enum ID {
        ATTACHED_SCENE,
        RENDERING_UNIT,
        BACKGROUND_RENDERING_UNIT
    }

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
