package com.github.rskupnik.storyteller.injection;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.TextEngine;
import com.github.rskupnik.storyteller.TextEngineImpl;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.renderingunits.factory.IRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.factory.RenderingUnitFactory;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class EngineModule {

    @Provides
    static IRenderingUnitFactory renderingUnitFactory() {
        return new RenderingUnitFactory();
    }

    @Provides
    @Singleton
    static TweenManager tweenManager() {
        return new TweenManager();
    }
}
