package com.github.rskupnik.storyteller.core.renderingunits.text.initializers;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;

public class TypewriterInitializer implements RenderingUnitInitializer {

    private final int charsPerSecond;

    public TypewriterInitializer(int charsPerSecond) {
        this.charsPerSecond = charsPerSecond;
    }

    public int getCharsPerSecond() {
        return charsPerSecond;
    }
}
