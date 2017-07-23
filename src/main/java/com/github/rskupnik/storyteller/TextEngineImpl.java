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
import com.github.rskupnik.storyteller.core.Renderer;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.renderingunits.background.factory.IBackgroundRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.text.factory.IRenderingUnitFactory;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.statefulobjects.objects.Scene;
import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;
import com.github.rskupnik.storyteller.statefulobjects.states.SceneState;
import com.github.rskupnik.storyteller.statefulobjects.states.StageState;
import com.github.rskupnik.storyteller.utils.SceneUtils;

import javax.inject.Inject;


public final class TextEngineImpl implements TextEngine {

    private StorytellerInjector injector;

    @Inject Renderer renderer;
    @Inject InputHandler inputHandler;
    @Inject Listeners listeners;
    @Inject TextEffects textEffects;
    @Inject TweenManager tweenManager;
    @Inject Scenes scenes;
    @Inject Stages stages;
    @Inject Commons commons;
    @Inject Lights lights;
    @Inject Clickables clickables;
    @Inject SceneUtils sceneUtils;
    @Inject IRenderingUnitFactory renderingUnitFactory;
    @Inject IBackgroundRenderingUnitFactory backgroundRenderingUnitFactory;

    @Inject
    public TextEngineImpl() {

    }

    void init(StorytellerInjector injector, Stage stage, BitmapFont font, Viewport viewport) {
        this.injector = injector;
        commons.font = font;
        commons.viewport = viewport;

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
    public void setClickListener(ClickListener clickListener) {
        listeners.clickListener = clickListener;
    }

    @Override
    public void setTextClickEffect(ClickEffect effect) {
        textEffects.clickEffect = effect;
    }

    @Override
    public void attachScene(String stageId, Scene scene) {
        StatefulScene statefulScene = new StatefulScene(scene, new SceneState());
        scenes.add(statefulScene);

        StatefulStage statefulStage = stages.find(stageId);
        if (statefulStage == null)
            throw new IllegalStateException("Cannot attach to stage "+stageId+" as it doesn't exist");

        if (statefulStage.state().getAttachedScene() != null)
            removeScene(statefulStage.state().getAttachedScene().obj());

        statefulStage.state().attachScene(statefulScene);
        statefulScene.state().attachStage(statefulStage);

        sceneUtils.transform(statefulScene);
    }

    @Override
    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    @Override
    public void removeScene(String id) {
        StatefulScene statefulScene = scenes.find(id);

        if (statefulScene != null) {
            clickables.removeScene(statefulScene);
            scenes.remove(statefulScene);

            StatefulStage stagePair = statefulScene.state().getAttachedStage();
            stagePair.state().attachScene(null);
            statefulScene.state().attachStage(null);
        }
    }

    @Override
    public void addStage(Stage stage) {
        StageState state = new StageState();
        if (stage.getRenderingUnitInitializer() != null)
            state.setRenderingUnit(renderingUnitFactory.create(injector, stage.getRenderingUnitInitializer()));
        if (stage.getBackgroundRenderingUnitInitializer() != null)
            state.setBackgroundRenderingUnit(backgroundRenderingUnitFactory.create(injector, stage.getBackgroundRenderingUnitInitializer()));
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
