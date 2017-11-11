package com.github.rskupnik.storyteller.core.effects;

public final class ShakeEffect extends TemporaryEffect {

    private final float intensity;
    private final int duration;

    public ShakeEffect(float intensity, int duration) {
        this.intensity = intensity;
        this.duration = duration;
    }

    public float getIntensity() {
        return intensity;
    }

    public int getDuration() {
        return duration;
    }
}
