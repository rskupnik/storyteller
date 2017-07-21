package com.github.rskupnik.storyteller.core.renderingunits.background.initializers;

import com.badlogic.gdx.graphics.Texture;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;

public class NormalMappedBackgroundInitializer implements RenderingUnitInitializer {

    private Texture normalMap;

    public NormalMappedBackgroundInitializer(Texture normalMap) {
        this.normalMap = normalMap;
    }

    public Texture getNormalMap() {
        return normalMap;
    }
}
