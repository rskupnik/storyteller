package com.github.rskupnik.storyteller.core.renderingunits.text.initializers;

import aurelienribon.tweenengine.TweenEquation;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;

public class LineFadeFloatInitializer implements RenderingUnitInitializer {

    private final TweenEquation equation;
    private final int duration, appearInterval, disappearInterval;

    public LineFadeFloatInitializer(TweenEquation equation, int duration, int appearInterval, int disappearInterval) {
        this.equation = equation;
        this.duration = duration;
        this.appearInterval = appearInterval;
        this.disappearInterval = disappearInterval;
    }

    public TweenEquation getEquation() {
        return equation;
    }

    public int getDuration() {
        return duration;
    }

    public int getAppearInterval() {
        return appearInterval;
    }

    public int getDisappearInterval() {
        return disappearInterval;
    }
}
