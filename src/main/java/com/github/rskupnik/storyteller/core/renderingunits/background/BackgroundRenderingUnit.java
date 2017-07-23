package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

public abstract class BackgroundRenderingUnit {

    private boolean firstRender = true;

    public abstract void init(RenderingUnitInitializer initializer);

    public void render(float delta, StatefulStage statefulStage) {
        if (firstRender) {
            firstRender = false;
            preFirstRender(statefulStage);
        }
    }

    public abstract void preFirstRender(StatefulStage statefulStage);
}
