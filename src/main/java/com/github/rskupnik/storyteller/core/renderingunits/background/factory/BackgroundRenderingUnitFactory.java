package com.github.rskupnik.storyteller.core.renderingunits.background.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.background.BackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.background.initializers.BasicBackgroundInitializer;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

import javax.inject.Inject;

public class BackgroundRenderingUnitFactory implements IBackgroundRenderingUnitFactory {

    @Inject
    public BackgroundRenderingUnitFactory() {

    }

    @Override
    public BackgroundRenderingUnit create(StorytellerInjector injector, RenderingUnitInitializer initializer) {
        BackgroundRenderingUnit ru = null;
        if (initializer instanceof BasicBackgroundInitializer)
            ru = injector.basicBgRU();
        else
            throw new IllegalArgumentException("Unknown RenderingUnitInitializer passed: "+initializer.getClass());

        if (ru != null)
            ru.init(initializer);

        return ru;
    }
}
