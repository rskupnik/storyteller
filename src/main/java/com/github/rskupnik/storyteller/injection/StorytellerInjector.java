package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.TextEngineImpl;
import com.github.rskupnik.storyteller.core.rendering.background.BasicBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.background.NormalMappedBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.text.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.text.TypewriterRenderingUnit;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = EngineModule.class)
@Singleton
public interface StorytellerInjector {
    TextEngineImpl engine();
    BasicBackgroundRenderingUnit basicBgRU();
    NormalMappedBackgroundRenderingUnit normalMappedBgRU();
    TypewriterRenderingUnit typewriterRU();
    LineFadeFloatRenderingUnit lineFFRU();
}
