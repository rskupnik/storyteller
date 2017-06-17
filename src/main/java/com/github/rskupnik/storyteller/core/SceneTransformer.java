package com.github.rskupnik.storyteller.core;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.rskupnik.storyteller.UndefinedOutput;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.utils.StageUtils;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

public final class SceneTransformer {

    @Inject private Commons commons;

    private UndefinedOutput undefinedOutput;

    public UndefinedOutput transform(ScenePair scenePair) {
        undefinedOutput = new UndefinedOutput();

        BitmapFont font = commons.font;
        StagePair stagePair = scenePair.internal().getAttachedStage();
        if (!scenePair.notNull() || font == null || !stagePair.notNull())
            return null;    // TODO: throw an exception here?

        Stage stage = stagePair.stage();

        int x = (int) stage.getTopLeft().x;
        int y = (int) stage.getTopLeft().y;
        boolean firstLine = true;
        for (Actor actor : scenePair.scene().getActors()) {
            ArrayList<Pair<Pair<GlyphLayout, Rectangle>, Vector2>> listOfGLRectPairs = new ArrayList<>();
            Pair<Actor, ArrayList<Pair<Pair<GlyphLayout, Rectangle>, Vector2>>> outerPair = new Pair<>(actor, listOfGLRectPairs);

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
                //font.draw(batch, GL_fragLine, x, y + actor.getInternalActor().getYOffset());    // Offsets used for tween animation

                // If it's clickable, produce a Rectangle
                Rectangle rect = null;
                if (actor.isClickable()) {
                    rect = new Rectangle(x, y, GL_fragLine.width, GL_fragLine.height);
                }
                listOfGLRectPairs.add(Pair.with(Pair.with(GL_fragLine, rect), new Vector2(x, y)));

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

            // Offsets are used to animate the actors with tweens
            //font.draw(batch, GL_body, x, y + actor.getInternalActor().getYOffset());    // Draw the main body of the text

            // The last run (line) of glyphs, used to extract new x and y for further rendering
            GlyphLayout.GlyphRun GR_last = GL_body.runs.get(GL_body.runs.size-1);

            // If it's clickable, produce a Rectangle
            Rectangle rect = null;
            Pair<Pair<GlyphLayout, Rectangle>, Vector2> tailPair = null;
            if (actor.isClickable()) {
                // If the text body spans multiple lines, we need to handle the last line as it might end in the middle of a line
                GlyphLayout.GlyphRun GR_tail = GR_last;
                int heightAdjust = 0;
                if (multilineGL(GL_body)) {
                    GR_tail = GL_body.runs.get(GL_body.runs.size-1);
                    rect = new Rectangle(stage.getTopLeft().x, stage.getTopLeft().y - GL_body.height - GR_tail.glyphs.get(0).height, GR_tail.width, GR_tail.glyphs.get(0).height);
                    //inputHandler.addClickable(scenePair.scene(), rect, actor);
                    heightAdjust = GR_tail.glyphs.get(0).height;

                    GlyphLayout GL_tail = new GlyphLayout(
                            font,
                            glyphsToText(new StringBuilder(GR_tail.glyphs.size), GR_tail).toString(),
                            actor.getColor() != null ? actor.getColor() : Color.WHITE,
                            GR_tail.width,
                            Align.left,
                            true
                    );
                    tailPair = Pair.with(Pair.with(GL_tail, rect), new Vector2(rect.x, rect.y));
                }
                rect = new Rectangle(x, y, (int) GL_body.width, (int) GL_body.height - heightAdjust);
                //inputHandler.addClickable(scenePair.scene(), rect, actor);
                GL_body.runs.removeIndex(GL_body.runs.size-1);
            }

            listOfGLRectPairs.add(Pair.with(Pair.with(GL_body, rect), new Vector2(x, y)));

            if (tailPair != null)
                listOfGLRectPairs.add(tailPair);

            // Extract new position
            x += (int) GR_last.width;
            if (GL_body.runs.size > 1) {
                y -= (int) GL_body.height;
                if (firstLine) {
                    firstLine = false;
                    y += GR_last.glyphs.get(0).height;
                }
            }

            undefinedOutput.add(outerPair);
        }   // end: for

        // GlyphLayouts mapped to an Actor
        // Rectangle (clickable area) mapped to an Actor

        undefinedOutput.print();

        return null;
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
