package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.Renderer;
import dagger.Component;

@Component(modules = EngineModule.class)
public interface EngineInjector {
    Renderer renderer();
}
