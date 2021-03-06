package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.RectangleAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.InputHandler;
import com.github.rskupnik.storyteller.core.SceneHandler;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.rendering.Renderer;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;
import com.github.rskupnik.storyteller.listeners.EventListener;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.statefulobjects.objects.Scene;
import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;
import com.github.rskupnik.storyteller.statefulobjects.states.StageState;

import javax.inject.Inject;


public final class TextEngineImpl implements TextEngine {

    private StorytellerInjector injector;

    @Inject Renderer renderer;
    @Inject InputHandler inputHandler;
    @Inject Listeners listeners;
    @Inject TextEffects textEffects;
    @Inject TweenManager tweenManager;
    @Inject Stages stages;
    @Inject Commons commons;
    @Inject Lights lights;
    @Inject SceneHandler sceneHandler;

    @Inject
    public TextEngineImpl() {

    }

    void init(StorytellerInjector injector, Stage stage, BitmapFont font, Viewport viewport) {
        this.injector = injector;
        commons.injector = injector;
        commons.font = font;
        commons.viewport = viewport;
        commons.worldDimensions = new Vector2(viewport.getWorldWidth(), viewport.getWorldHeight());

        addStage(stage);

        renderer.init();
        inputHandler.init(renderer.getCamera());

        Tween.registerAccessor(StatefulActor.class, new ActorAccessor());
        Tween.registerAccessor(Vector2.class, new Vector2Accessor());
        Tween.registerAccessor(Color.class, new ColorAccessor());
        Tween.registerAccessor(Rectangle.class, new RectangleAccessor());
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
    public void setEventListener(EventListener eventListener) {
        listeners.eventListener = eventListener;
    }

    @Override
    public void setTextClickEffect(ClickEffect effect) {
        textEffects.clickEffect = effect;
    }

    @Override
    public void attachScene(String stageId, Scene scene) {
        sceneHandler.attachScene(stageId, scene);
    }

    @Override
    public void removeScene(Scene scene) {
        sceneHandler.removeScene(scene);
    }

    @Override
    public void removeScene(String id) {
        sceneHandler.removeScene(id);
    }

    @Override
    public void addStage(Stage stage) {
        StageState state = new StageState();
        stages.add(new StatefulStage(stage, state));
    }

    @Override
    public void setAmbientLight(AmbientLight light) {
        commons.ambientLight = light;
    }

    @Override
    public void addLight(Light light) {
        lights.add(light);
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return inputHandler;
    }

}
