package com.github.rskupnik.storyteller.core.renderingunits.text.factory;

import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.TypewriterInitializer;
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
