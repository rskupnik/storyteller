package com.github.rskupnik.storyteller.core.lighting;

import com.badlogic.gdx.math.Vector3;

public final class Light {

    private Vector3 position = new Vector3(0, 0, 0.075f);
    private Vector3 color = new Vector3(1, 1, 1);
    private Vector3 falloff = new Vector3(.4f, 3f, 20f);
    private float intensity = 1;
    private boolean attached = false;

    private Light() {

    }

    public static Builder newLight() {
        return new Builder();
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getColor() {
        return color;
    }

    public Vector3 getFalloff() {
        return falloff;
    }

    public float getIntensity() {
        return intensity;
    }

    public boolean isAttached() {
        return attached;
    }

    private void setPosition(Vector3 position) {
        this.position = position;
    }

    private void setColor(Vector3 color) {
        this.color = color;
    }

    private void setFalloff(Vector3 falloff) {
        this.falloff = falloff;
    }

    private void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    private void setAttached(boolean attached) {
        this.attached = attached;
    }

    public static final class Builder {

        private Light light;

        private Builder() {
            this.light = new Light();
        }

        public Light build() {
            return light;
        }

        public Builder position(Vector3 position) {
            light.setPosition(position);
            return this;
        }

        public Builder color(Vector3 color) {
            light.setColor(color);
            return this;
        }

        public Builder falloff(Vector3 falloff) {
            light.setFalloff(falloff);
            return this;
        }

        public Builder intensity(float intensity) {
            light.setIntensity(intensity);
            return this;
        }

        public Builder attached() {
            light.setAttached(true);
            return this;
        }
    }
}
