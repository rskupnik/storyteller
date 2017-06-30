package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.aggregates.Listeners;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.structs.Clickable;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handles input and triggers effects on actors.
 */
public final class InputHandler implements com.badlogic.gdx.InputProcessor {

    @Inject private Listeners listeners;
    @Inject private Clickables clickables;
    @Inject private TextEffects textEffects;
    @Inject private TweenManager tweenManager;

    private Camera camera;

    public void init(Camera camera) {
        this.camera = camera;
    }

    @Override
    public boolean keyDown(int i) {
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
        System.out.println("Clicked: "+touched);
        for (Map.Entry<ScenePair, List<Clickable>> sceneToClickableListEntry : clickables.entrySet()) {
            for (Clickable clickable : sceneToClickableListEntry.getValue()) {
                Rectangle rect = clickable.rectangle();
                if (touched.x >= rect.getX() && touched.x <= rect.getX() + rect.getWidth() &&
                        touched.y <= rect.getY() && touched.y >= rect.getY() - rect.getHeight()) {
                    ClickListener listener = listeners.clickListener;
                    if (listener != null) {
                        listener.onActorClicked(clickable.actor(), new Vector2(touched.x, touched.y), button);
                    }

                    if (clickable.actor().getClickEffect() != null) {    // Use Actor's Click Effect first
                        clickable.actor().getClickEffect().produceTween(clickable.actor().getInternalActor()).start(tweenManager);
                    } else {
                        StagePair stagePair = sceneToClickableListEntry.getKey().internal().getAttachedStage();
                        if (stagePair.notNull() && stagePair.stage().getTextEffects().clickEffect != null) {    // Use Stage's click effect next
                            stagePair.stage().getTextEffects().clickEffect.produceTween(clickable.actor().getInternalActor()).start(tweenManager);
                        } else if (textEffects.clickEffect != null) {   // Fallback to engine's click effect
                            textEffects.clickEffect.produceTween(clickable.actor().getInternalActor()).start(tweenManager);
                        }
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
