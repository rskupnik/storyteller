package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.TypewriterRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.TypewriterInitializer;
import com.google.inject.Injector;

public class RenderingUnitFactory implements IRenderingUnitFactory {

    @Override
    public RenderingUnit create(Injector injector, RenderingUnitInitializer initializer) {
        RenderingUnit ru = null;
        if (initializer instanceof LineFadeFloatInitializer)
            ru = injector.getInstance(LineFadeFloatRenderingUnit.class);
        else if (initializer instanceof TypewriterInitializer)
            ru = injector.getInstance(TypewriterRenderingUnit.class);
        else
            throw new IllegalArgumentException("Unknown RenderingUnitInitializer passed: "+initializer.getClass());

        if (ru != null)
            ru.init(initializer);

        return ru;
    }
}
