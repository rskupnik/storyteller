package com.github.rskupnik.storyteller.core.effects;

import com.badlogic.gdx.math.Vector2;

public final class ShakeEffectHandler {

    /**
     * Generates position noise to be added to the original position.
     * It is supposed to be called on each frame to adjust the noise.
     */
    public Vector2 generatePositionNoise(ShakeEffect shakeEffect) {
        return new Vector2(0, 0);
    }
}
