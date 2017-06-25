package com.github.rskupnik.storyteller.core.scenetransform;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.utils.StageUtils;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;

import java.util.ArrayList;
import java.util.List;

public final class SceneTransformer {

    @Inject private Commons commons;
    @Inject private Clickables clickables;

    private int x, y;
    private boolean firstLine;

    public TransformedScene transform(ScenePair scenePair) {
        return transform(new TransformedScene(), scenePair);
    }

    public TransformedScene transform(TransformedScene output, ScenePair scenePair) {
        BitmapFont font = commons.font;
        StagePair stagePair = scenePair.internal().getAttachedStage();
        if (!scenePair.notNull() || font == null || !stagePair.notNull())
            return null;    // TODO: throw an exception here?

        Stage stage = stagePair.stage();
        Vector2 cursor = output.getSavedCursorPosition();
        Boolean savedIsFirstLine = output.getSavedIsFirstLine();

        if (cursor != null) {
            x = (int) cursor.x;
            y = (int) cursor.y;
        } else {
            x = (int) stage.getTopLeft().x;
            y = (int) stage.getTopLeft().y;
        }
        firstLine = savedIsFirstLine != null ? savedIsFirstLine : true;

        for (Actor actor : scenePair.scene().getActors()) {
            if (actor.getInternalActor().isTransformed())
                continue;

            trn(output, actor, scenePair, font, stage);
        }

        output.setSavedCursorPosition(new Vector2(x, y));
        output.setSavedIsFirstLine(firstLine);

        return output;
    }

    private void trn(TransformedScene output, Actor actor, ScenePair scenePair, BitmapFont font, Stage stage) {
        List<Fragment> fragments = new ArrayList<>();

        // We need to produce the whole text first to see how LibGDX plans to structure it and do some adjusting if needed
        GlyphLayout GL_wholeText = new GlyphLayout(
                font,
                actor.getText(),
                actor.getColor() != null ? actor.getColor() : Color.WHITE,
                StageUtils.calcRemainingWidth(stage, x),
                Align.left,
                true
        );


        GlyphLayout GL_body = GL_wholeText; // The main body of the text (without the adjustments below), used to extract x and y

        /* If we don't start rendering from a new line and the produced GL spans multiple lines we need to
           handle the first, fragmented line individually and then the rest of the text body. If we didn't, we'd get
           a text that only uses the remaining width even for new lines. */
        if (StageUtils.notStartOfLine(stage, x) && multilineGL(GL_wholeText)) {
            // Need to deal individually with the first, fragmented line and then continue with the rest of the body as usual
            GlyphLayout.GlyphRun GR_fragLine = GL_wholeText.runs.get(0);
            String lineEndText = glyphsToText(new StringBuilder(GR_fragLine.glyphs.size), GR_fragLine).toString();
            GlyphLayout GL_fragLine = new GlyphLayout(
                    font,
                    lineEndText,
                    actor.getColor() != null ? actor.getColor() : Color.WHITE,
                    StageUtils.calcRemainingWidth(stage, x),
                    Align.left,
                    true
            );

            // If it's clickable, produce a Rectangle
            Rectangle rect = null;
            if (actor.isClickable()) {
                rect = new Rectangle(x, y, GL_fragLine.width, GL_fragLine.height);
                clickables.addClickable(scenePair, rect, actor);
            }

            fragments.add((Fragment) new Fragment()
                    .with("glyphLayout", GL_fragLine)
                    .with("clickableArea", rect)
                    .with("position", new Vector2(x, y))
            );

            // Adjust x and y after the fragmented line to continue with the rest of the text
            x = (int) stage.getTopLeft().x;
            y -= (int) GL_fragLine.height + GR_fragLine.glyphs.get(0).height;

            // Combine the rest of the GlyphLayout to a String
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < GL_wholeText.runs.size; i++) {
                GlyphLayout.GlyphRun glyphRun = GL_wholeText.runs.get(i);
                glyphsToText(sb, glyphRun);
            }
            String restText = sb.toString();

            // Produce a new GlyphLayout with the proper width this time
            GL_body = new GlyphLayout(
                    font,
                    restText,
                    actor.getColor() != null ? actor.getColor() : Color.WHITE,
                    stage.getWidth(),
                    Align.left,
                    true
            );

        }

        // The last run (line) of glyphs, used to extract new x and y for further rendering
        GlyphLayout.GlyphRun GR_last = GL_body.runs.get(GL_body.runs.size-1);

        // If it's clickable, produce a Rectangle
        Rectangle rect = null;
        Fragment tailFragment = null;
        int GLBodyLines = GL_body.runs.size;    // Save the no of lines because we remove the last line but we need the pre-removal size in position adjustement
        if (actor.isClickable()) {
            // If the text body spans multiple lines, we need to handle the last line as it might end in the middle of a line
            GlyphLayout.GlyphRun GR_tail = GR_last;
            if (multilineGL(GL_body)) {
                GR_tail = GL_body.runs.get(GL_body.runs.size-1);
                rect = new Rectangle(stage.getTopLeft().x, stage.getTopLeft().y - GL_body.height - GR_tail.glyphs.get(0).height, GR_tail.width, GR_tail.glyphs.get(0).height);
                clickables.addClickable(scenePair, rect, actor);

                GlyphLayout GL_tail = new GlyphLayout(
                        font,
                        glyphsToText(new StringBuilder(GR_tail.glyphs.size), GR_tail).toString(),
                        actor.getColor() != null ? actor.getColor() : Color.WHITE,
                        GR_tail.width,
                        Align.left,
                        true
                );

                tailFragment = (Fragment) new Fragment()
                        .with("glyphLayout", GL_tail)
                        .with("clickableArea", rect)
                        .with("position", new Vector2(rect.x, rect.y)
                        );

                GL_body.runs.removeIndex(GL_body.runs.size-1);  // Remove the tail from the body
            }
            rect = new Rectangle(x, y, (int) GL_body.width, (int) GL_body.height);
            clickables.addClickable(scenePair, rect, actor);

        }

        fragments.add((Fragment) new Fragment()
                .with("glyphLayout", GL_body)
                .with("clickableArea", rect)
                .with("position", new Vector2(x, y))
        );

        if (tailFragment != null)
            fragments.add(tailFragment);

        // Extract new position
        x += (int) GR_last.width;
        if (GLBodyLines > 1) {
            y -= (int) GL_body.height;
            if (firstLine) {    // This is compensating for the fact we start from top-left of the scene but text draws from bottom-left of each line
                firstLine = false;
                // TODO: BUG - if the first glyph is a space, this won't work correctly - need to handle line height extraction in a better way
                y += GR_last.glyphs.get(0).height;
            }
        }

        actor.getInternalActor().setTransformed(true);

        output.addActor(actor, fragments);
    }

    private StringBuilder glyphsToText(StringBuilder sb, GlyphLayout.GlyphRun input) {
        StringBuilder buffer = sb;
        Array<BitmapFont.Glyph> glyphs = input.glyphs;
        int i = 0;
        for(int n = glyphs.size; i < n; ++i) {
            BitmapFont.Glyph g = (BitmapFont.Glyph)glyphs.get(i);
            buffer.append((char)g.id);
        }
        return buffer;
    }

    /**
     * Checks whether the generated GlyphLayout spans multiple lines.
     */
    private boolean multilineGL(GlyphLayout GL) {
        return GL.runs.size > 1;
    }
}
