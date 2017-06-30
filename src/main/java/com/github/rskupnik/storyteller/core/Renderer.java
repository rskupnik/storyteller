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
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.effects.inout.IOEffect;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Scenes;
import com.github.rskupnik.storyteller.aggregates.Stages;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import org.javatuples.Pair;

import java.util.List;

public final class Renderer {

    @Inject private InputHandler inputHandler;
    @Inject private Scenes scenes;
    @Inject private Stages stages;
    @Inject private Commons commons;
    @Inject private Clickables clickables;
    @Inject private TweenManager tweenManager;
    @Inject private SceneUtils sceneUtils;

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
        drawScenes(delta);
        batch.end();
    }

    private void drawScenes(float delta) {
        for (ScenePair scenePair : scenes) {
            draw(delta, scenePair);
        }
    }

    private void draw(float delta, ScenePair scenePair) {
        if (!scenePair.notNull())
            return;

        if (scenePair.scene().isDirty()) {
            sceneUtils.transform(scenePair);
            System.out.println("SCENE TRANSFORMED");
        }

        TransformedScene data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        BitmapFont font = commons.font;
        if (font == null)
            return;

        StagePair stagePair = scenePair.internal().getAttachedStage();
        if (!stagePair.notNull())
            throw new IllegalStateException("Cannot render a scene without a stage. Scene passed: "+scenePair.scene().getId());

        // If an IOEffect is defined, use it, otherwise continue to default rendering
        IOEffect ioEffect = stagePair.stage().getIOEffect();
        if (ioEffect != null) {
            ioEffect.render(delta, batch, font, tweenManager, scenePair);
            scenePair.scene().setDirty(false);
            scenePair.internal().wasDrawn();
            return;
        }

        // This is the default rendering used if no IOEffect is defined
        // TODO: Pull this out to a BasicRenderer class or sth to be consistent
        for (Pair<Actor, List<Fragment>> actorToDataPair : data.getData()) {
            Actor actor = actorToDataPair.getValue0();
            for (Fragment fragment : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) fragment.get("glyphLayout");
                Rectangle rectangle = (Rectangle) fragment.get("clickableArea");
                Vector2 position = (Vector2) fragment.get("position");

                if (GL == null || position == null)
                    continue;

                // Draw the GL
                font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
            }
        }
        scenePair.scene().setDirty(false);
        scenePair.internal().wasDrawn();
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public Camera getCamera() {
        return camera;
    }
}
