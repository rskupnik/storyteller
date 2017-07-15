package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

public interface IRenderingUnitFactory {

    RenderingUnit create(StorytellerInjector injector, RenderingUnitInitializer initializer);
}
