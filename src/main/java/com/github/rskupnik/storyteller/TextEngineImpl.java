package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.peripheral.internals.InternalActor;
import com.github.rskupnik.storyteller.peripheral.internals.InternalScene;
import com.github.rskupnik.storyteller.peripheral.internals.InternalStage;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import com.google.inject.Injector;


public final class TextEngineImpl implements TextEngine {

    private Injector injector;

    @Inject private Renderer renderer;
    @Inject private InputHandler inputHandler;
    @Inject private Listeners listeners;
    @Inject private TextEffects textEffects;
    @Inject private TweenManager tweenManager;
    @Inject private Scenes scenes;
    @Inject private Stages stages;
    @Inject private Commons commons;
    @Inject private Clickables clickables;
    @Inject private SceneTransformer sceneTransformer;

    void init(Injector injector, Stage stage, BitmapFont font) {
        this.injector = injector;
        commons.font = font;

        addStage(stage);

        renderer.init(font);
        inputHandler.init(renderer.getCamera());

        Tween.registerAccessor(InternalActor.class, new ActorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Color.class, new ColorAccessor());
    }

    @Override
    public void render(float delta) {
        tweenManager.update(delta);
        renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        renderer.resize(width, height);
    }

    @Override
    public void setClickListener(ClickListener clickListener) {
        listeners.clickListener = clickListener;
    }

    @Override
    public void setTextClickEffect(ClickEffect effect) {
        textEffects.clickEffect = effect;
    }

    @Override
    public void attachScene(String stageId, Scene scene) {
        ScenePair scenePair = new ScenePair(scene, new InternalScene());
        scenes.add(scenePair);

        StagePair stagePair = stages.find(stageId);
        if (stagePair == null)
            throw new IllegalStateException("Cannot attach to stage "+stageId+" as it doesn't exist");

        if (stagePair.internal().getAttachedScene() != null)
            removeScene(stagePair.internal().getAttachedScene().scene());

        stagePair.internal().attachScene(scenePair);
        scenePair.internal().attachStage(stagePair);

        TransformedScene transformedScene = sceneTransformer.transform(scenePair);
        if (stagePair.stage().getIOEffect() != null)
            stagePair.stage().getIOEffect().getChain().apply(transformedScene);
        scenePair.internal().setTransformedScene(transformedScene);
    }

    @Override
    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    @Override
    public void removeScene(String id) {
        ScenePair scenePair = scenes.find(id);

        if (scenePair != null) {
            clickables.removeScene(scenePair);
            scenes.remove(scenePair);

            StagePair stagePair = scenePair.internal().getAttachedStage();
            stagePair.internal().attachScene(null);
            scenePair.internal().attachStage(null);
        }
    }

    @Override
    public void addStage(Stage stage) {
        stages.add(new StagePair(stage, new InternalStage()));
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return inputHandler;
    }

}
