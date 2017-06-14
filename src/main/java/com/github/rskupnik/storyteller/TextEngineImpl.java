package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.google.inject.Inject;
import com.google.inject.Injector;


public class TextEngineImpl implements TextEngine {

    private Injector injector;

    @Inject
    EngineState state;

    void init(Injector injector, String areaId, Rectangle area, BitmapFont font) {
        this.injector = injector;

        this.state.engine = this;

        new Renderer(state, areaId, area, font);
        new InputHandler(state, state.renderer.getCamera());

        Tween.registerAccessor(InternalActor.class, new ActorAccessor());
    }

    @Override
    public void render(float delta) {
        state.tweenManager.update(delta);
        state.renderer.render(delta);
    }

    @Override
    public void resize(int width, int height) {
        state.renderer.resize(width, height);
    }

    @Override
    public void setClickListener(ClickListener clickListener) {
        state.listeners.clickListener = clickListener;
    }

    @Override
    public void setTextClickEffect(TextEffect effect) {
        state.effects.textClickEffect = effect;
    }

    @Override
    public void addScene(Scene scene) {
        state.scenes.put(scene.getId(), scene);
    }

    @Override
    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    @Override
    public void removeScene(String id) {
        Scene scene = state.scenes.get(id);
        if (scene != null) {
            state.inputHandler.removeScene(scene);
            state.scenes.remove(id);
        }
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return state.inputHandler;
    }

}
