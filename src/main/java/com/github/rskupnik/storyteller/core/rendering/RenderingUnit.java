package com.github.rskupnik.storyteller.core.rendering;

import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

public abstract class RenderingUnit {

    private boolean firstRender = true;

    public void render(float delta, StatefulStage stage) {
        if (firstRender) {
            firstRender = false;
            preFirstRender(stage);
        }
    }

    public abstract void preFirstRender(StatefulStage stage);
}
