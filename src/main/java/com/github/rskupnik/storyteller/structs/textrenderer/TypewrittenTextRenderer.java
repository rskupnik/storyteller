package com.github.rskupnik.storyteller.structs.textrenderer;

import com.github.rskupnik.storyteller.core.sceneextend.CharSequenceExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ColorExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;

public class TypewrittenTextRenderer extends TextRenderer {

    private final int charsPerSecond;
    private final boolean affectedByLight;

    public TypewrittenTextRenderer(int charsPerSecond, boolean affectedByLight) {
        this.charsPerSecond = charsPerSecond;
        this.affectedByLight = affectedByLight;
    }

    public int getCharsPerSecond() {
        return charsPerSecond;
    }

    public boolean isAffectedByLight() {
        return affectedByLight;
    }

    @Override
    public ExtenderChain getExtenderChain() {
        return ExtenderChain.from(new CharSequenceExtender(), new ColorExtender());
    }
}
