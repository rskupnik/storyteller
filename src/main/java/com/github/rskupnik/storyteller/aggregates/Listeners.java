package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.listeners.EventListener;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Aggregate class that holds all listeners registered with the engine.
 */
@Singleton
public final class Listeners {
    public EventListener eventListener;

    @Inject
    public Listeners() {

    }
}
