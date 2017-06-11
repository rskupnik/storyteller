package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.*;
import com.github.rskupnik.storyteller.listeners.ClickListener;

import java.util.*;

public class InputHandler implements com.badlogic.gdx.InputProcessor {

    private EngineState state;

    private Camera camera;
    private Map<Rectangle, Actor> clickablesMap = new HashMap<>();

    InputHandler(EngineState state, Camera camera) {
        this.camera = camera;
        this.state = state;
        state.inputHandler = this;
    }

    void addClickable(Rectangle rectangle, Actor actor) {
        clickablesMap.put(rectangle, actor);
    }

    void clearClickables() {
        clickablesMap.clear();
    }

    @Override
    public boolean keyDown(int i) {
        System.out.println("KEY DOWN!");
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector3 touched = camera.unproject(new Vector3(screenX, screenY, 0));
        for (Map.Entry<Rectangle, Actor> entry : clickablesMap.entrySet()) {
            Rectangle rect = entry.getKey();
            if (touched.x >= rect.getX() && touched.x <= rect.getX() + rect.getWidth() &&
                    touched.y <= rect.getY() && touched.y >= rect.getY() - rect.getHeight()) {
                ClickListener listener = state.listeners.clickListener;
                if (listener != null) {
                    listener.onActorClicked(entry.getValue(), new Vector2(touched.x, touched.y), button);
                }

                if (state.effects.textClickEffect != null) {
                    state.effects.textClickEffect.produceTween(entry.getValue().getInternalActor()).start(state.tweenManager);
                }
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int x, int y) {
        /*Vector3 touched = camera.unproject(new Vector3(x, y, 0));
        for (Map.Entry<Rectangle, Actor> entry : clickablesMap.entrySet()) {
            Rectangle rect = entry.getKey();
            if (touched.x >= rect.getX() && touched.x <= rect.getX() + rect.getWidth() &&
                    touched.y <= rect.getY() && touched.y >= rect.getY() - rect.getHeight()) {
                Tween.to(entry.getValue().getInternalActor(), ActorAccessor.POSITION_Y, 0.4f)
                        .target(2.0f)
                        .ease(Quad.IN)
                        .repeatYoyo(1, 0)
                        .start(state.tweenManager);
            }
        }*/
        return false;
    }

    @Override
    public boolean scrolled(int i) {
        return false;
    }
}
