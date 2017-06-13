package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.Renderer;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class EngineModule {

    @Provides
    @Singleton
    public static Renderer renderer() {
        return new Renderer();
    }
}
