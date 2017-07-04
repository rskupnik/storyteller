package com.github.rskupnik.storyteller.core.renderingunits.factory;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.google.inject.Injector;

public interface IRenderingUnitFactory {

    RenderingUnit create(Injector injector, RenderingUnitInitializer initializer);
}
