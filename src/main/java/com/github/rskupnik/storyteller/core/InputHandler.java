package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.*;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.EngineState;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.aggregates.Listeners;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.google.inject.Inject;

import java.util.*;

public class InputHandler implements com.badlogic.gdx.InputProcessor {

    @Inject private EngineState state;
    @Inject private Listeners listeners;
    @Inject private TextEffects textEffects;
    @Inject private TweenManager tweenManager;

    private Camera camera;
    private Map<Scene, Map<Rectangle, Actor>> clickablesMap = new HashMap<>();

    public void init(Camera camera) {
        this.camera = camera;
    }

    public void addClickable(Scene scene, Rectangle rectangle, Actor actor) {
        Map<Rectangle, Actor> innerMap = clickablesMap.get(scene);
        if (innerMap == null)
            innerMap = new HashMap<>();
        innerMap.put(rectangle, actor);
        clickablesMap.put(scene, innerMap);
    }

    public void clearClickables() {
        clickablesMap.clear();
    }

    public void clearClickables(Scene scene) {
        clickablesMap.get(scene).clear();
    }

    public void removeScene(Scene scene) {
        clearClickables(scene);
        clickablesMap.remove(scene);
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
        for (Map.Entry<Scene, Map<Rectangle, Actor>> sceneToInnerEntry : clickablesMap.entrySet()) {
            for (Map.Entry<Rectangle, Actor> entry : sceneToInnerEntry.getValue().entrySet()) {
                Rectangle rect = entry.getKey();
                if (touched.x >= rect.getX() && touched.x <= rect.getX() + rect.getWidth() &&
                        touched.y <= rect.getY() && touched.y >= rect.getY() - rect.getHeight()) {
                    ClickListener listener = listeners.clickListener;
                    if (listener != null) {
                        listener.onActorClicked(entry.getValue(), new Vector2(touched.x, touched.y), button);
                    }

                    if (entry.getValue().getClickEffect() != null) {
                        entry.getValue().getClickEffect().produceTween(entry.getValue().getInternalActor()).start(tweenManager);
                    } else if (textEffects.clickEffect != null) {
                        textEffects.clickEffect.produceTween(entry.getValue().getInternalActor()).start(tweenManager);
                    }
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