package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.TextEngine;
import com.github.rskupnik.storyteller.TextEngineImpl;
import com.github.rskupnik.storyteller.core.renderingunits.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.TypewriterRenderingUnit;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = EngineModule.class)
@Singleton
public interface StorytellerInjector {
    TextEngineImpl engine();
    LineFadeFloatRenderingUnit lineFFRU();
    TypewriterRenderingUnit typewriterRU();
}
