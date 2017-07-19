package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

public abstract class BackgroundRenderingUnit {

    public abstract void init(RenderingUnitInitializer initializer);

    public abstract void render(float delta, StatefulStage statefulStage);
}
