package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

public class TextEngineImpl implements TextEngine {

    private final EngineState state;

    public TextEngineImpl(Rectangle area, BitmapFont font) {
        this.state = new EngineState();
        this.state.engine = this;

        new Renderer(state, area, font);
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
    public void setScene(Scene scene) {
        state.currentScene = scene;
        state.firstSceneDraw = true;
        state.inputHandler.clearClickables();
    }

    @Override
    public com.badlogic.gdx.InputProcessor getInputProcessor() {
        return state.inputHandler;
    }

}
