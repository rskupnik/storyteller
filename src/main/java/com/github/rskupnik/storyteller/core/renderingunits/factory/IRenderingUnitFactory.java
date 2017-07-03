package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;

public interface IRenderingUnitFactory {

    RenderingUnit create(RenderingUnitInitializer initializer);
}
