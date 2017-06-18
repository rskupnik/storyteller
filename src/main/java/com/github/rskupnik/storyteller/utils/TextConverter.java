package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;

public final class TextConverter {

    public static StringBuilder glyphRunToString(StringBuilder sb, GlyphLayout.GlyphRun input) {
        StringBuilder buffer = sb;
        Array<BitmapFont.Glyph> glyphs = input.glyphs;
        int i = 0;
        for(int n = glyphs.size; i < n; ++i) {
            BitmapFont.Glyph g = (BitmapFont.Glyph)glyphs.get(i);
            buffer.append((char)g.id);
        }
        return buffer;
    }

    public static StringBuilder glyphLayoutToString(StringBuilder sb, GlyphLayout input) {
        if (input.runs.size == 0) return sb;
        StringBuilder buffer = sb;
        for (int i = 0, n = input.runs.size; i < n; i++) {
            glyphRunToString(sb, input.runs.get(i));
            buffer.append('\n');
        }
        buffer.setLength(buffer.length() - 1);
        return buffer;
    }
}
