package com.github.rskupnik.storyteller.structs.textrenderer;

import aurelienribon.tweenengine.TweenEquation;
import com.github.rskupnik.storyteller.core.sceneextend.*;

public class LineFadeTextRenderer extends TextRenderer {

    private final TweenEquation equation;
    private final int duration, appearInterval, disappearInterval;
    private final boolean affectedByLight;

    public LineFadeTextRenderer(TweenEquation equation, int duration, int appearInterval, int disappearInterval, boolean affectedByLight) {
        this.equation = equation;
        this.duration = duration;
        this.appearInterval = appearInterval;
        this.disappearInterval = disappearInterval;
        this.affectedByLight = affectedByLight;
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

    public boolean isAffectedByLight() {
        return affectedByLight;
    }

    @Override
    public ExtenderChain getExtenderChain() {
        return ExtenderChain.from(new LineExtender(), new ColorToTransparentExtender(), new PullDownExtender(), new StateFlagsExtender());
    }
}
