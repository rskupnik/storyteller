package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.TypewriterRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.TypewriterInitializer;

public class RenderingUnitFactory implements IRenderingUnitFactory {

    @Override
    public RenderingUnit create(RenderingUnitInitializer initializer) {
        if (initializer instanceof LineFadeFloatInitializer)
            return new LineFadeFloatRenderingUnit((LineFadeFloatInitializer) initializer);
        else if (initializer instanceof TypewriterInitializer)
            return new TypewriterRenderingUnit((TypewriterInitializer) initializer);
        else
            throw new IllegalArgumentException("Unknown RenderingUnitInitializer passed: "+initializer.getClass());
    }
}
