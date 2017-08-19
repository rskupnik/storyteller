package com.github.rskupnik.storyteller.core;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.SceneSwaps;
import com.github.rskupnik.storyteller.aggregates.Stages;
import com.github.rskupnik.storyteller.core.rendering.RenderingUnitPicker;
import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import com.github.rskupnik.storyteller.structs.textrenderer.TextRenderer;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import org.javatuples.Pair;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public final class Renderer {

    @Inject Stages stages;
    @Inject Commons commons;
    @Inject SceneUtils sceneUtils;
    @Inject SceneSwaps sceneSwaps;
    @Inject SceneHandler sceneHandler;
    @Inject RenderingUnitPicker renderingUnitPicker;

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    @Inject
    public Renderer() {

    }

    public void init() {
        //commons.font.getData().markupEnabled = true;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false);
        viewport = commons.viewport;
        viewport.setCamera(camera);
        viewport.apply(true);

        commons.batch = batch;
        commons.defaultShader = SpriteBatch.createDefaultShader();
    }

    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        drawStages(delta);
    }

    private void drawStages(float delta) {
        for (StatefulStage statefulStage : stages) {
            draw(delta, statefulStage);
        }
    }

    private void draw(float delta, StatefulStage statefulStage) {
        if (StatefulStage.isNull(statefulStage))
            throw new IllegalStateException("Cannot render a scene without a stage. Stage passed: "+statefulStage.obj().getId());

        Background background = statefulStage.obj().getBackground();
        if (background != null) {
            com.github.rskupnik.storyteller.core.rendering.RenderingUnit renderingUnit = renderingUnitPicker.pick(background, statefulStage);
            if (renderingUnit != null)
                renderingUnit.render(delta, statefulStage);
        }

        StatefulScene statefulScene = statefulStage.state().getAttachedScene();

        if (StatefulScene.isNull(statefulScene))
            return;

        // Handle Scene Swap process
        if (sceneSwaps.containsKey(statefulStage)) {
            if (statefulScene.state().isExitSequenceFinished()) {
                StatefulScene newScene = sceneSwaps.get(statefulStage);
                sceneHandler.removeScene(statefulScene.obj());
                sceneSwaps.remove(statefulStage);
                statefulScene = sceneHandler.activateScene(statefulStage, newScene);
            }
        }

        // Draw the background image
        /**BackgroundRenderingUnit backgroundRenderingUnit = statefulStage.state().getBackgroundRenderingUnit();
        if (backgroundRenderingUnit != null) {
            backgroundRenderingUnit.render(delta, statefulStage);
            return;
        }**/

        if (statefulScene.obj().isDirty()) {
            sceneUtils.transform(statefulScene);
        }

        TransformedScene data = statefulScene.state().getTransformedScene();
        if (data == null)
            return;

        BitmapFont font = commons.font;
        if (font == null)
            return;

        // If a RenderingUnit is defined, use it, otherwise continue to default rendering
        /*RenderingUnit renderingUnit = statefulStage.state().getRenderingUnit();
        if (renderingUnit != null) {
            renderingUnit.render(delta, statefulScene);
            statefulScene.obj().setDirty(false);
            statefulScene.state().wasDrawn();
            return;
        }*/

        TextRenderer textRenderer = statefulStage.obj().getTextRenderer();
        if (textRenderer != null) {
            com.github.rskupnik.storyteller.core.rendering.RenderingUnit renderingUnit = renderingUnitPicker.pick(statefulStage.obj().getTextRenderer(), statefulStage);
            if (renderingUnit != null) {
                renderingUnit.render(delta, statefulStage);
                statefulScene.obj().setDirty(false);
                statefulScene.state().wasDrawn();
                return;
            }
        }

        // This is the default rendering used if no RenderingUnit is defined
        // TODO: Pull this out to a BasicRenderer class or sth to be consistent
        batch.begin();
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            for (Fragment fragment : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) fragment.get(FragmentId.GLYPH_LAYOUT);
                Rectangle rectangle = (Rectangle) fragment.get(FragmentId.CLICKABLE_AREA);
                Vector2 position = (Vector2) fragment.get(FragmentId.POSITION);

                if (GL == null || position == null)
                    continue;

                // Draw the GL
                font.draw(batch, GL, position.x, position.y + actor.state().getYOffset());
            }
        }
        statefulScene.obj().setDirty(false);
        statefulScene.state().wasDrawn();

        batch.end();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Camera getCamera() {
        return camera;
    }
}
