package com.github.rskupnik.storyteller.injection;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.*;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;

public final class EngineModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(TextEngine.class).to(TextEngineImpl.class).in(Scopes.SINGLETON);
        bind(Renderer.class).in(Scopes.SINGLETON);
        bind(InputHandler.class).in(Scopes.SINGLETON);
        bind(Listeners.class).in(Scopes.SINGLETON);
        bind(TextEffects.class).in(Scopes.SINGLETON);
        bind(Scenes.class).in(Scopes.SINGLETON);
        bind(Stages.class).in(Scopes.SINGLETON);
        bind(Commons.class).in(Scopes.SINGLETON);
        bind(Clickables.class).in(Scopes.SINGLETON);
        bind(SceneTransformer.class).in(Scopes.SINGLETON);
        bind(SceneUtils.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    TweenManager tweenManagerProvider() {
        return new TweenManager();
    }
}
