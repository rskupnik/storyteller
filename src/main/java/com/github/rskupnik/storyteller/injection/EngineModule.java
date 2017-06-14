package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.EngineState;
import com.github.rskupnik.storyteller.Renderer;
import com.github.rskupnik.storyteller.TextEngine;
import com.github.rskupnik.storyteller.TextEngineImpl;
import com.github.rskupnik.storyteller.aggregates.Listeners;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(EngineState.class).in(Scopes.SINGLETON);
        bind(TextEngine.class).to(TextEngineImpl.class).in(Scopes.SINGLETON);
        bind(Renderer.class).in(Scopes.SINGLETON);
        bind(Listeners.class).in(Scopes.SINGLETON);
    }
}
