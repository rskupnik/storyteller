package com.github.rskupnik.storyteller.injection;

import aurelienribon.tweenengine.TweenManager;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class EngineModule {

    @Provides
    @Singleton
    static TweenManager tweenManager() {
        return new TweenManager();
    }
}
