package com.github.rskupnik.storyteller.core.renderingunits.initializers;

public class TypewriterInitializer implements RenderingUnitInitializer {

    private final int charsPerSecond;

    public TypewriterInitializer(int charsPerSecond) {
        this.charsPerSecond = charsPerSecond;
    }

    public int getCharsPerSecond() {
        return charsPerSecond;
    }
}
