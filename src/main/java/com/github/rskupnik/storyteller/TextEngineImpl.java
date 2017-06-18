package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.SceneTransformer;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.*;
import com.github.rskupnik.storyteller.peripheral.internals.InternalActor;
import com.github.rskupnik.storyteller.peripheral.internals.InternalScene;
import com.github.rskupnik.storyteller.peripheral.internals.InternalStage;
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
    @Inject private SceneTransformer sceneTransformer;
    @Inject private Commons commons;
    @Inject private Clickables clickables;

    void init(Injector injector, Stage stage, BitmapFont font) {
        this.injector = injector;
        commons.font = font;

        stages.add(new StagePair(stage, new InternalStage()));

        renderer.init(font);
        inputHandler.init(renderer.getCamera());

        Tween.registerAccessor(InternalActor.class, new ActorAccessor());
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

        stagePair.internal().attachScene(scenePair);
        scenePair.internal().attachStage(stagePair);

        commons.transformedScene = sceneTransformer.transform(scenePair);
        if (commons.appearEffect != null)
            commons.appearEffect.transform(commons.transformedScene);
    }

    @Override
    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    @Override
    public void removeScene(String id) {
        ScenePair scenePair = scenes.find(id);

        if (scenePair != null) {
            clickables.removeScene(scenePair.scene());
            scenes.remove(scenePair);

            StagePair stagePair = scenePair.internal().getAttachedStage();
            stagePair.internal().attachScene(null);
            scenePair.internal().attachStage(null);
        }
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return inputHandler;
    }

}
