package com.github.rskupnik.storyteller.core.rendering;

import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.rendering.background.BasicBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.background.NormalMappedBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.text.LineFadeFloatRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.text.TextRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.text.TypewriterRenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.backgrounds.BasicBackground;
import com.github.rskupnik.storyteller.structs.backgrounds.NormalMappedBackground;
import com.github.rskupnik.storyteller.structs.textrenderer.LineFadeTextRenderer;
import com.github.rskupnik.storyteller.structs.textrenderer.TextRenderer;
import com.github.rskupnik.storyteller.structs.textrenderer.TypewrittenTextRenderer;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public final class RenderingUnitPicker {

    @Inject Commons commons;

    private Map<StatefulStage, RenderingUnit> backgroundRenderingUnits = new HashMap<>();
    private Map<StatefulStage, RenderingUnit> textRenderingUnits = new HashMap<>();

    @Inject
    public RenderingUnitPicker() {

    }

    public RenderingUnit pick(Background background, StatefulStage stage) {
        RenderingUnit current = backgroundRenderingUnits.get(stage);
        if (background instanceof BasicBackground) {
            if (current == null || !(current instanceof BasicBackgroundRenderingUnit)) {
                current = commons.injector.basicBgRU2();
                backgroundRenderingUnits.put(stage, current);
            }
        } else if (background instanceof NormalMappedBackground) {
            if (current == null || !(current instanceof NormalMappedBackgroundRenderingUnit)) {
                current = commons.injector.normalMappedBgRU2();
                backgroundRenderingUnits.put(stage, current);
            }
        }
        return current;
    }

    public RenderingUnit pick(TextRenderer textRenderer, StatefulStage stage) {
        RenderingUnit current = textRenderingUnits.get(stage);
        if (textRenderer instanceof TypewrittenTextRenderer) {
            if (current == null || !(current instanceof TypewriterRenderingUnit)) {
                current = commons.injector.typewriterRU2();
                ((TextRenderingUnit) current).init(textRenderer);
                textRenderingUnits.put(stage, current);
            }
        } else if (textRenderer instanceof LineFadeTextRenderer) {
            if (current == null || !(current instanceof LineFadeFloatRenderingUnit)) {
                current = commons.injector.lineFFRU2();
                ((TextRenderingUnit) current).init(textRenderer);
                textRenderingUnits.put(stage, current);
            }
        }
        return current;
    }
}
