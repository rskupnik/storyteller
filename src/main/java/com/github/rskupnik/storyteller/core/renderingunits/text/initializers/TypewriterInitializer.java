package com.github.rskupnik.storyteller.core.renderingunits.text.initializers;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;

public class TypewriterInitializer implements RenderingUnitInitializer {

    private final int charsPerSecond;
    private final boolean affectedByLight;

    public TypewriterInitializer(int charsPerSecond, boolean affectedByLight) {
        this.charsPerSecond = charsPerSecond;
        this.affectedByLight = affectedByLight;
    }

    public int getCharsPerSecond() {
        return charsPerSecond;
    }

    public boolean isAffectedByLight() {
        return affectedByLight;
    }
}
