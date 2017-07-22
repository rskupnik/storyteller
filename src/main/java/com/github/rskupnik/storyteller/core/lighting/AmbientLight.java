package com.github.rskupnik.storyteller.core.lighting;

import com.badlogic.gdx.math.Vector3;

public final class AmbientLight {

    private final Vector3 color;
    private final float intensity;

    public AmbientLight(Vector3 color, float intensity) {
        this.color = color;
        this.intensity = intensity;
    }

    public Vector3 getColor() {
        return color;
    }

    public float getIntensity() {
        return intensity;
    }
}
