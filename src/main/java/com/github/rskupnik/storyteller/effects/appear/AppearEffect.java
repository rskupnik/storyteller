package com.github.rskupnik.storyteller.effects.appear;

import com.github.rskupnik.storyteller.effects.transformers.EffectTransformer;

/**
 * Group of classes that decide how text will appear on the Stage.
 * Probably needs to override the default render?
 */
public abstract class AppearEffect implements EffectTransformer {
    public abstract void render();
}
