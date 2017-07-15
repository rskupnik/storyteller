package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.effects.click.ClickEffect;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Aggregate class that holds all text effects registered in the engine.
 */
@Singleton
public final class TextEffects {
    public ClickEffect clickEffect;

    @Inject
    public TextEffects() {

    }
}
