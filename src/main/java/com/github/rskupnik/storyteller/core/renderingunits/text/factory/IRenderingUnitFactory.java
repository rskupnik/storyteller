package com.github.rskupnik.storyteller.core.renderingunits.text.factory;

import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

public interface IRenderingUnitFactory {

    RenderingUnit create(StorytellerInjector injector, RenderingUnitInitializer initializer);
}
