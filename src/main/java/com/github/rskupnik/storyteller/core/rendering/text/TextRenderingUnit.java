package com.github.rskupnik.storyteller.core.rendering.text;

import com.github.rskupnik.storyteller.core.rendering.RenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.textrenderer.TextRenderer;

public abstract class TextRenderingUnit extends RenderingUnit {

    @Override
    public void render(float delta, StatefulStage stage) {
        super.render(delta, stage);
    }

    public abstract void init(TextRenderer textRenderer);

    public abstract void reset();
}
