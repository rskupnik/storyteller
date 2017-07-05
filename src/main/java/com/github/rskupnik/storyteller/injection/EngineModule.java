package com.github.rskupnik.storyteller.injection;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.*;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.renderingunits.factory.IRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.factory.RenderingUnitFactory;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.google.inject.*;

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
        bind(NamedOffsets.class).in(Scopes.SINGLETON);
        bind(SceneTransformer.class).in(Scopes.SINGLETON);
        bind(SceneUtils.class).in(Scopes.SINGLETON);
        bind(IRenderingUnitFactory.class).to(RenderingUnitFactory.class);
        /*install(new FactoryModuleBuilder()
                .implement(IRenderingUnit.class, LineFadeFloatRenderingUnit.class)
                .build(IRenderingUnitFactory.class)
        );*/

    }

    @Provides
    @Singleton
    TweenManager tweenManagerProvider() {
        return new TweenManager();
    }
}
