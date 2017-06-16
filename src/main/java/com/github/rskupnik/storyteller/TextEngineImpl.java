package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.aggregates.Listeners;
import com.github.rskupnik.storyteller.aggregates.Scenes;
import com.github.rskupnik.storyteller.aggregates.Stages;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.InternalActor;
import com.github.rskupnik.storyteller.peripheral.InternalScene;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.google.inject.Inject;
import com.google.inject.Injector;


public class TextEngineImpl implements TextEngine {

    private Injector injector;

    @Inject private Renderer renderer;
    @Inject private InputHandler inputHandler;
    @Inject private Listeners listeners;
    @Inject private TextEffects textEffects;
    @Inject private TweenManager tweenManager;
    @Inject private Scenes scenes;
    @Inject private Stages stages;

    void init(Injector injector, Stage stage, BitmapFont font) {
        this.injector = injector;

        stages.add(stage);

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
    public void setTextClickEffect(TextEffect effect) {
        textEffects.clickEffect = effect;
    }

    @Override
    public void addScene(Scene scene) {
        scenes.add(new ScenePair(scene, new InternalScene()));
    }

    @Override
    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    @Override
    public void removeScene(String id) {
        ScenePair scenePair = null;
        for (ScenePair sp : scenes) {
            if (sp.scene().getId().equals(id)) {
                scenePair = sp;
                break;
            }
        }

        if (scenePair != null) {
            inputHandler.removeScene(scenePair.scene());
            scenes.remove(scenePair);
        }
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return inputHandler;
    }

}
