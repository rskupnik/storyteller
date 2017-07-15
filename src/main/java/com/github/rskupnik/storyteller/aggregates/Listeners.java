package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.listeners.ClickListener;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Aggregate class that holds all listeners registered with the engine.
 */
@Singleton
public final class Listeners {
    public ClickListener clickListener;

    @Inject
    public Listeners() {

    }
}
