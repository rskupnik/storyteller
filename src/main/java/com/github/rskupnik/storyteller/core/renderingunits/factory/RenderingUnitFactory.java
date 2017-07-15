package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.TypewriterInitializer;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

import javax.inject.Inject;

public class RenderingUnitFactory implements IRenderingUnitFactory {

    @Inject
    public RenderingUnitFactory() {

    }

    @Override
    public RenderingUnit create(StorytellerInjector injector, RenderingUnitInitializer initializer) {
        RenderingUnit ru = null;
        if (initializer instanceof LineFadeFloatInitializer)
            ru = injector.lineFFRU();
        else if (initializer instanceof TypewriterInitializer)
            ru = injector.typewriterRU();
        else
            throw new IllegalArgumentException("Unknown RenderingUnitInitializer passed: "+initializer.getClass());

        if (ru != null)
            ru.init(initializer);

        return ru;
    }
}
