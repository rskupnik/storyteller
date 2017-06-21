package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
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
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.effects.appear.AppearEffect;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Scenes;
import com.github.rskupnik.storyteller.aggregates.Stages;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.utils.StageUtils;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import org.javatuples.Pair;
import org.javatuples.Triplet;

import java.util.ArrayList;

public final class Renderer {

    @Inject private InputHandler inputHandler;
    @Inject private Scenes scenes;
    @Inject private Stages stages;
    @Inject private Commons commons;
    @Inject private Clickables clickables;
    @Inject private TweenManager tweenManager;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;
    private BitmapFont font;

    public void init(BitmapFont font) {
        this.font = font;
        font.getData().markupEnabled = true;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply(true);
    }

    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        //drawScenes(delta);
        newDrawScenes(delta);
        batch.end();
    }

    private void newDrawScenes(float delta) {
        for (ScenePair scenePair : scenes) {
            newDraw(delta, scenePair);
        }
    }

    private void newDraw(float delta, ScenePair scenePair) {
        if (!scenePair.notNull())
            return;

        TransformedScene<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>> data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        BitmapFont font = commons.font;
        if (font == null)
            return;

        StagePair stagePair = scenePair.internal().getAttachedStage();
        if (!stagePair.notNull())
            throw new IllegalStateException("Cannot render a scene without a stage. Scene passed: "+scenePair.scene().getId());

        // If an AppearEffect is defined, use it, otherwise continue to default rendering
        AppearEffect appearEffect = stagePair.stage().getAppearEffect();
        if (appearEffect != null) {
            appearEffect.render(delta, batch, font, tweenManager);
            scenePair.internal().wasDrawn();
            return;
        }

        // This is the default rendering used if no AppearEffect is defined
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : data) {
            Actor actor = actorToDataPair.getValue0();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = actorData.getValue1();
                Vector2 position = actorData.getValue2();

                if (GL == null || position == null)
                    continue;

                // Draw the GL
                font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
            }
        }
        scenePair.internal().wasDrawn();
    }

    @Deprecated
    private void drawScenes(float delta) {
        for (ScenePair scenePair : scenes) {
            StagePair stagePair = scenePair.internal().getAttachedStage();
            drawScene(delta, stagePair.stage(), scenePair);
            scenePair.internal().wasDrawn();
        }
    }

    @Deprecated
    private void drawScene(float delta, Stage stage, ScenePair scenePair) {
        if (!scenePair.notNull() || font == null || stage == null)
            return;

        int x = (int) stage.getTopLeft().x;
        int y = (int) stage.getTopLeft().y;
        boolean firstLine = true;
        for (Actor actor : scenePair.scene().getActors()) {
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
                String lineEndText = TextConverter.glyphRunToString(new StringBuilder(GR_fragLine.glyphs.size), GR_fragLine, font.getData().markupEnabled).toString();
                GlyphLayout GL_fragLine = new GlyphLayout(
                        font,
                        lineEndText,
                        actor.getColor() != null ? actor.getColor() : Color.WHITE,
                        StageUtils.calcRemainingWidth(stage, x),
                        Align.left,
                        true
                );
                font.draw(batch, GL_fragLine, x, y + actor.getInternalActor().getYOffset());    // Offsets used for tween animation

                // If it's clickable, produce a Rectangle
                if (scenePair.internal().isFirstDraw() && actor.isClickable()) {
                    Rectangle rect = new Rectangle(x, y, GL_fragLine.width, GL_fragLine.height);
                    clickables.addClickable(scenePair, rect, actor);
                }

                // Adjust x and y after the fragmented line to continue with the rest of the text
                x = (int) stage.getTopLeft().x;
                y -= (int) GL_fragLine.height + GR_fragLine.glyphs.get(0).height;

                // Combine the rest of the GlyphLayout to a String
                StringBuilder sb = new StringBuilder();
                for (int i = 1; i < GL_wholeText.runs.size; i++) {
                    GlyphLayout.GlyphRun glyphRun = GL_wholeText.runs.get(i);
                    TextConverter.glyphRunToString(sb, glyphRun, font.getData().markupEnabled);
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
            font.draw(batch, GL_body, x, y + actor.getInternalActor().getYOffset());    // Draw the main body of the text

            // The last run (line) of glyphs, used to extract new x and y for further rendering
            GlyphLayout.GlyphRun GR_last = GL_body.runs.get(GL_body.runs.size-1);

            // If it's clickable, produce a Rectangle
            if (scenePair.internal().isFirstDraw() && actor.isClickable()) {
                // If the text body spans multiple lines, we need to handle the last line as it might end in the middle of a line
                GlyphLayout.GlyphRun GR_tail = GR_last;
                int heightAdjust = 0;
                if (multilineGL(GL_body)) {
                    GR_tail = GL_body.runs.get(GL_body.runs.size-1);
                    Rectangle rect = new Rectangle(stage.getTopLeft().x, stage.getTopLeft().y - GL_body.height - GR_tail.glyphs.get(0).height, GR_tail.width, GR_tail.glyphs.get(0).height);
                    clickables.addClickable(scenePair, rect, actor);
                    heightAdjust = GR_tail.glyphs.get(0).height;
                }
                Rectangle rect = new Rectangle(x, y, (int) GL_body.width, (int) GL_body.height - heightAdjust);
                clickables.addClickable(scenePair, rect, actor);
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
    }

    /**
     * Checks whether the generated GlyphLayout spans multiple lines.
     */
    private boolean multilineGL(GlyphLayout GL) {
        return GL.runs.size > 1;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Camera getCamera() {
        return camera;
    }
}
