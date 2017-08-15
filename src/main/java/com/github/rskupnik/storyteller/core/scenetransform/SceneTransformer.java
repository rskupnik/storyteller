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
import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.github.rskupnik.storyteller.utils.StageUtils;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

@Singleton
public final class SceneTransformer {

    @Inject Commons commons;
    @Inject Clickables clickables;

    private int x, y;
    private boolean firstLine, newLine;

    @Inject
    public SceneTransformer() {

    }

    public TransformedScene transform(StatefulScene statefulScene) {
        return transform(new TransformedScene(), statefulScene);
    }

    public TransformedScene transform(TransformedScene output, StatefulScene statefulScene) {
        BitmapFont font = commons.font;
        StatefulStage statefulStage = statefulScene.state().getAttachedStage();
        if (StatefulScene.isNull(statefulScene) || font == null || StatefulStage.isNull(statefulStage))
            return null;    // TODO: throw an exception here?

        Stage stage = statefulStage.obj();
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

        for (StatefulActor actor : statefulScene.obj().getActors()) {
            if (actor.state().isTransformed())
                continue;

            transformActor(output, actor, statefulScene, font, stage);
        }

        output.setSavedCursorPosition(new Vector2(x, y));
        output.setSavedIsFirstLine(firstLine);

        return output;
    }

    private void transformActor(TransformedScene output, StatefulActor actor, StatefulScene statefulScene, BitmapFont font, Stage stage) {
        // Handle a newline - simply adjust placement markers and set the actor as processed
        if (actor.obj().getText().equals("\n")) {
            adjustCursorToNewLine((int) stage.getTopLeft().x);
            actor.state().setTransformed(true);
            return;
        }

        List<Fragment> fragments = new ArrayList<>();

        // We need to produce the whole text first to see how LibGDX plans to structure it and do some adjusting if needed
        GlyphLayout glyphLayoutWhole = new GlyphLayout(
                font,
                actor.obj().getText(),
                actor.obj().getColor() != null ? actor.obj().getColor() : Color.WHITE,
                StageUtils.calcRemainingWidth(stage, x),
                Align.left,
                true
        );

        // If GlyphLayout has multiple lines, handle the first one independently
        if (glyphLayoutWhole.runs.size > 1) {
            // This needs to be done because this first line might only take a fragment of
            // the remaining width but we want the rest of the lines to be re-calculated
            // with the whole available width. So we need to handle this line individually,
            // rebuild glyphLayoutWhole without it and let it carry on.

            GlyphLayout.GlyphRun line = glyphLayoutWhole.runs.get(0);

            // Process the first line and add the resulting Fragment into the final list
            Fragment fragment = transformLine(line, font, actor, stage, statefulScene);
            fragments.add(fragment);

            // Rebuild the GlyphLayout without the first line as we just processed it
            glyphLayoutWhole = rebuildGLWithoutFirstLine(glyphLayoutWhole, font, actor, stage);

            // Reset the cursor to point to a new line
            adjustCursorToNewLine((int) stage.getTopLeft().x);
        }

        // Main loop - processes each line (excluding the first one if it was treated individually above)
        int c = 0;
        for (GlyphLayout.GlyphRun line : glyphLayoutWhole.runs) {
            if (c > 0) {    // If this is not the first line, reset cursor to point to a new line
                adjustCursorToNewLine((int) stage.getTopLeft().x);
            }

            // Build the Fragment and add it to the output list
            Fragment fragment = transformLine(line, font, actor, stage, statefulScene);
            fragments.add(fragment);

            // After processing a fragment, move the x marker by the fragment's width
            x += (int) line.width;

            c++;
        }

        // Save the actor as transformed and link the produced fragments to it
        actor.state().setTransformed(true);
        output.addActor(actor, fragments);
    }

    /**
     * Transforms a single GlyphRun line into a Fragment by calculating it's position and producing a standalone
     * GlyphLayout. Also produces a Clickable if required.
     */
    private Fragment transformLine(GlyphLayout.GlyphRun line, BitmapFont font, StatefulActor actor, Stage stage, StatefulScene statefulScene) {
        // Transform the GlyphRun into text
        String lineText = glyphsToText(new StringBuilder(line.glyphs.size), line).toString();

        // Transform the text into a GlyphLayout
        GlyphLayout GL_fragLine = new GlyphLayout(
                font,
                lineText,
                actor.obj().getColor() != null ? actor.obj().getColor() : Color.WHITE,
                StageUtils.calcRemainingWidth(stage, x),
                Align.left,
                true
        );

        // If it's clickable, produce a Clickable
        Rectangle rect = null;
        if (actor.obj().isClickable())
            rect = createAndRegisterClickable(GL_fragLine, statefulScene, actor);

        // Create and return a Fragment
        return (Fragment) new Fragment()
                .with(FragmentId.GLYPH_LAYOUT, GL_fragLine)
                .with(FragmentId.CLICKABLE_AREA, rect)
                .with(FragmentId.POSITION, new Vector2(x, y));
    }

    /**
     * Creates a Clickable and returns the covered area as a Rectangle.
     * Apart from creating, it also immediately register the new Clickable in the clickables aggregate.
     */
    private Rectangle createAndRegisterClickable(GlyphLayout glyphLayout, StatefulScene statefulScene, StatefulActor actor) {
        Rectangle rect = new Rectangle(x, y, glyphLayout.width, glyphLayout.height);
        clickables.addClickable(statefulScene, rect, actor, glyphLayout);

        return rect;
    }

    /**
     * Rebuilds a GlyphLayout but skips the first line.
     */
    private GlyphLayout rebuildGLWithoutFirstLine(GlyphLayout glyphLayout, BitmapFont font, StatefulActor actor, Stage stage) {
        // Combine the rest of the GlyphLayout to a String, skipping the first line
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < glyphLayout.runs.size; i++) {
            GlyphLayout.GlyphRun glyphRun = glyphLayout.runs.get(i);
            glyphsToText(sb, glyphRun);
        }
        String restText = sb.toString();

        // Produce a new GlyphLayout from the newly created string
        return new GlyphLayout(
                font,
                restText,
                actor.obj().getColor() != null ? actor.obj().getColor() : Color.WHITE,
                stage.getWidth(),
                Align.left,
                true
        );
    }

    /**
     * Y marker is adjust by line height extract from the font,
     * X marker is set to the provided parameter.
     */
    private void adjustCursorToNewLine(int xpos) {
        x = xpos;
        y -= SceneUtils.extractLineHeightFromFont(commons.font);
    }

    /**
     * Transforms a GlyphRun into a StringBuilder filled with text of the GlyphRun.
     */
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
}
