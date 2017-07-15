package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnit;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Scenes;
import com.github.rskupnik.storyteller.aggregates.Stages;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.javatuples.Pair;

import java.util.List;

@Singleton
public final class Renderer {

    @Inject InputHandler inputHandler;
    @Inject Scenes scenes;
    @Inject Stages stages;
    @Inject Commons commons;
    @Inject Clickables clickables;
    @Inject TweenManager tweenManager;
    @Inject SceneUtils sceneUtils;

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
        viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), camera);
        viewport.apply(true);

        commons.batch = batch;
    }

    public void render(float delta) {
        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        drawScenes(delta);
        batch.end();
    }

    private void drawScenes(float delta) {
        for (StatefulScene statefulScene : scenes) {
            draw(delta, statefulScene);
        }
    }

    private void draw(float delta, StatefulScene statefulScene) {
        if (!statefulScene.notNull())
            return;

        if (statefulScene.obj().isDirty()) {
            sceneUtils.transform(statefulScene);
            System.out.println("SCENE TRANSFORMED");
        }

        TransformedScene data = statefulScene.state().getTransformedScene();
        if (data == null)
            return;

        BitmapFont font = commons.font;
        if (font == null)
            return;

        StatefulStage statefulStage = statefulScene.state().getAttachedStage();
        if (!statefulStage.notNull())
            throw new IllegalStateException("Cannot render a scene without a stage. Scene passed: "+statefulScene.obj().getId());

        // If an LineFadeFloatInitializer is defined, use it, otherwise continue to default rendering
        RenderingUnit renderingUnit = statefulStage.state().getRenderingUnit();
        if (renderingUnit != null) {
            renderingUnit.render(delta, statefulScene);
            statefulScene.obj().setDirty(false);
            statefulScene.state().wasDrawn();
            return;
        }

        // This is the default rendering used if no LineFadeFloatInitializer is defined
        // TODO: Pull this out to a BasicRenderer class or sth to be consistent
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            for (Fragment fragment : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) fragment.get("glyphLayout");
                Rectangle rectangle = (Rectangle) fragment.get("clickableArea");
                Vector2 position = (Vector2) fragment.get("position");

                if (GL == null || position == null)
                    continue;

                // Draw the GL
                font.draw(batch, GL, position.x, position.y + actor.state().getYOffset());
            }
        }
        statefulScene.obj().setDirty(false);
        statefulScene.state().wasDrawn();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Camera getCamera() {
        return camera;
    }
}
