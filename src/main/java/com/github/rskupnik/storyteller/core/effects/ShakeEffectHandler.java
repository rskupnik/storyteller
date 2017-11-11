package com.github.rskupnik.storyteller.core.effects;

import com.badlogic.gdx.math.Vector2;

import java.util.Random;

public final class ShakeEffectHandler {

    //region Variables
    private float elapsed, duration, intensity;
    private Random random = new Random();
    //endregion

    //region Public methods
    public void shake(float intensity, float duration) {
        this.elapsed = 0;
        this.duration = duration / 1000f;
        this.intensity = intensity;
    }

    public Vector2 update(float delta) {
        // Only shake when required.
        if (elapsed < duration) {
            // Calculate the amount of shake based on how long it has been shaking already
            float currentPower = intensity * ((duration - elapsed) / duration);
            float x = (random.nextFloat() - 0.5f) * currentPower;
            float y = (random.nextFloat() - 0.5f) * currentPower;

            // Increase the elapsed time by the delta provided.
            elapsed += delta;

            return new Vector2(x, y);
        }
        return null;
    }
    //endregion
}
