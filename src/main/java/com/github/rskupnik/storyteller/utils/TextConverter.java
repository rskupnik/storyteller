package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Array;

public final class TextConverter {

    public static StringBuilder glyphRunToString(StringBuilder sb, GlyphLayout.GlyphRun input, boolean withColor) {
        StringBuilder buffer = sb;

        if (withColor) {
            Color color = input.color;
            if (color != null) {
                sb.append("[#");
                sb.append(color.toString());
                sb.append("]");
            }
        }

        Array<BitmapFont.Glyph> glyphs = input.glyphs;
        int i = 0;
        for(int n = glyphs.size; i < n; ++i) {
            BitmapFont.Glyph g = (BitmapFont.Glyph)glyphs.get(i);
            buffer.append((char)g.id);
        }

        if (withColor && input.color != null)
            sb.append("[]");

        return buffer;
    }

    public static StringBuilder glyphLayoutToString(StringBuilder sb, GlyphLayout input, boolean withColor) {
        if (input.runs.size == 0) return sb;
        StringBuilder buffer = sb;
        for (int i = 0, n = input.runs.size; i < n; i++) {
            glyphRunToString(sb, input.runs.get(i), withColor);
            buffer.append('\n');
        }
        buffer.setLength(buffer.length() - 1);
        return buffer;
    }
}
