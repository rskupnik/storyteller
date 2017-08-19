package com.github.rskupnik.storyteller.injection;

import com.github.rskupnik.storyteller.TextEngineImpl;
import com.github.rskupnik.storyteller.core.rendering.background.BasicBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.background.NormalMappedBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.TypewriterRenderingUnit;
import com.github.rskupnik.storyteller.structs.textrenderer.LineFadeTextRenderer;
import com.github.rskupnik.storyteller.structs.textrenderer.TypewrittenTextRenderer;
import dagger.Component;

import javax.inject.Singleton;

@Component(modules = EngineModule.class)
@Singleton
public interface StorytellerInjector {
    TextEngineImpl engine();
    LineFadeFloatRenderingUnit lineFFRU();
    TypewriterRenderingUnit typewriterRU();
    BasicBackgroundRenderingUnit basicBgRU2();
    NormalMappedBackgroundRenderingUnit normalMappedBgRU2();
    com.github.rskupnik.storyteller.core.rendering.text.TypewriterRenderingUnit typewriterRU2();
    com.github.rskupnik.storyteller.core.rendering.text.LineFadeFloatRenderingUnit lineFFRU2();
}
