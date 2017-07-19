package com.github.rskupnik.storyteller.injection;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.core.renderingunits.background.factory.BackgroundRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.background.factory.IBackgroundRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.text.factory.IRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.text.factory.RenderingUnitFactory;
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
    static IBackgroundRenderingUnitFactory backgroundRenderingUnitFactory() {
        return new BackgroundRenderingUnitFactory();
    }

    @Provides
    @Singleton
    static TweenManager tweenManager() {
        return new TweenManager();
    }
}
