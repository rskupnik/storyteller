package com.github.rskupnik.storyteller.core.renderingunits.background.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.background.BackgroundRenderingUnit;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

public interface IBackgroundRenderingUnitFactory {
    BackgroundRenderingUnit create(StorytellerInjector injector, RenderingUnitInitializer initializer);
}
