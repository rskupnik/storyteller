package com.github.rskupnik.storyteller;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.HashMap;
import java.util.Map;

final class Renderer {

    private EngineState state;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private Map<String, Rectangle> areas = new HashMap<>();
    private BitmapFont font;
    private Vector2 areaTopLeft;
    private int areaWidth;

    Renderer(EngineState state, String areaId, Rectangle area, BitmapFont font) {
        this.state = state;
        state.renderer = this;
        this.areas.put(areaId, area);
        this.font = font;

        // Calculate helper variables for the area
        this.areaTopLeft = new Vector2(area.x, area.y + area.height);
        this.areaWidth = (int) area.width;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply(true);
    }

    void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        drawScene(delta);
        batch.end();
    }

    private void drawScene(float delta) {
        Scene scene = state.currentScene;
        if (scene == null || font == null)
            return;

        int x = (int) areaTopLeft.x;
        int y = (int) areaTopLeft.y;
        boolean firstLine = true;
        for (Actor actor : scene.getActors()) {
            // We need to produce the whole text first to see how LibGDX plans to structure it and do some adjusting if needed
            GlyphLayout GL_wholeText = new GlyphLayout(
                    font,
                    actor.getText(),
                    actor.getColor() != null ? actor.getColor() : Color.WHITE,
                    calcRemainingWidth(x),
                    Align.left,
                    true
            );


            GlyphLayout GL_body = GL_wholeText; // The main body of the text (without the adjustments below), used to extract x and y

            /* If we don't start rendering from a new line and the produced GL spans multiple lines we need to
               handle the first, fragmented line individually and then the rest of the text body. If we didn't, we'd get
               a text that only uses the remaining width even for new lines. */
            if (notStartOfLine(x) && multilineGL(GL_wholeText)) {
                // Need to deal individually with the first, fragmented line and then continue with the rest of the body as usual
                GlyphLayout.GlyphRun GR_fragLine = GL_wholeText.runs.get(0);
                String lineEndText = glyphsToText(new StringBuilder(GR_fragLine.glyphs.size), GR_fragLine).toString();
                GlyphLayout GL_fragLine = new GlyphLayout(
                        font,
                        lineEndText,
                        actor.getColor() != null ? actor.getColor() : Color.WHITE,
                        calcRemainingWidth(x),
                        Align.left,
                        true
                );
                font.draw(batch, GL_fragLine, x, y + actor.getInternalActor().getYOffset());    // Offsets used for tween animation

                // If it's clickable, produce a Rectangle
                if (state.firstSceneDraw && actor.isClickable()) {
                    Rectangle rect = new Rectangle(x, y, GL_fragLine.width, GL_fragLine.height);
                    state.inputHandler.addClickable(rect, actor);
                }

                // Adjust x and y after the fragmented line to continue with the rest of the text
                x = (int) areaTopLeft.x;
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
                        areaWidth,
                        Align.left,
                        true
                );

            }

            // Offsets are used to animate the actors with tweens
            font.draw(batch, GL_body, x, y + actor.getInternalActor().getYOffset());    // Draw the main body of the text

            // The last run (line) of glyphs, used to extract new x and y for further rendering
            GlyphLayout.GlyphRun GR_last = GL_body.runs.get(GL_body.runs.size-1);

            // If it's clickable, produce a Rectangle
            if (state.firstSceneDraw && actor.isClickable()) {
                // If the text body spans multiple lines, we need to handle the last line as it might end in the middle of a line
                GlyphLayout.GlyphRun GR_tail = GR_last;
                int heightAdjust = 0;
                if (multilineGL(GL_body)) {
                    GR_tail = GL_body.runs.get(GL_body.runs.size-1);
                    Rectangle rect = new Rectangle(areaTopLeft.x, areaTopLeft.y - GL_body.height - GR_tail.glyphs.get(0).height, GR_tail.width, GR_tail.glyphs.get(0).height);
                    state.inputHandler.addClickable(rect, actor);
                    heightAdjust = GR_tail.glyphs.get(0).height;
                }
                Rectangle rect = new Rectangle(x, y, (int) GL_body.width, (int) GL_body.height - heightAdjust);
                state.inputHandler.addClickable(rect, actor);
            }

            // Extract new position
            x += (int) GR_last.width;
            if (GL_body.runs.size > 1) {
                y -= (int) GL_body.height;
                if (firstLine) {
                    firstLine = false;
                    y += GR_last.glyphs.get(0).height;
                }
            }
        }   // end: for
        state.firstSceneDraw = false;
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
     * Calculates the remaining width available to fill with text in the current text row.
     */
    private int calcRemainingWidth(int currentX) {
        return areaWidth - (currentX - (int) areaTopLeft.x);
    }

    /**
     * Checks whether x points to the beginning of a new text line or somewhere in an existing one.
     */
    private boolean notStartOfLine(int x) {
        return x != (int) areaTopLeft.x;
    }

    /**
     * Checks whether the generated GlyphLayout spans multiple lines.
     */
    private boolean multilineGL(GlyphLayout GL) {
        return GL.runs.size > 1;
    }

    void resize(int width, int height) {
        viewport.update(width, height);
    }

    Camera getCamera() {
        return camera;
    }
}
