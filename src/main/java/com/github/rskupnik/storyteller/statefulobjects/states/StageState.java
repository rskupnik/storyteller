package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.structs.State;

import static com.github.rskupnik.storyteller.statefulobjects.states.StageState.ID.ATTACHED_SCENE;
import static com.github.rskupnik.storyteller.statefulobjects.states.StageState.ID.RENDERING_UNIT;

public final class StageState extends State<StageState.ID> {

    protected enum ID {
        ATTACHED_SCENE,
        RENDERING_UNIT
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
}
