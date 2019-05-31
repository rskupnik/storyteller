package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.aggregates.Listeners;
import com.github.rskupnik.storyteller.aggregates.NamedOffsets;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.listeners.EventListener;
import com.github.rskupnik.storyteller.structs.Clickable;
import com.github.rskupnik.storyteller.structs.State;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.List;
import java.util.Map;

/**
 * Handles input and triggers effects on actors.
 */
@Singleton
public final class InputHandler implements com.badlogic.gdx.InputProcessor {

    @Inject Listeners listeners;
    @Inject Clickables clickables;
    @Inject TextEffects textEffects;
    @Inject TweenManager tweenManager;
    @Inject NamedOffsets namedOffsets;

    private Camera camera;

    @Inject
    public InputHandler() {

    }

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
        for (Map.Entry<StatefulScene, List<Clickable>> sceneToClickableListEntry : clickables.entrySet()) {
            Vector2 offset = namedOffsets.get("LFF-offset-"+sceneToClickableListEntry.getKey().obj().getId());
            for (Clickable clickable : sceneToClickableListEntry.getValue()) {
                Rectangle rect = clickable.rectangle();
                StatefulActor actor = clickable.actor();
                State state = clickable.state();

                if (rect == null || actor == null)
                    continue;

                if (touched.x >= rect.getX() &&
                    touched.x <= rect.getX() + rect.getWidth() &&
                    touched.y <= rect.getY() + (offset != null ? offset.y : 0) &&
                    touched.y >= rect.getY() - rect.getHeight() + (offset != null ? offset.y : 0)) {

                    EventListener listener = listeners.eventListener;
                    if (listener != null) {
                        listener.onActorClicked(actor.obj(), new Vector2(touched.x, touched.y), button);
                    }

                    if (actor.obj().getClickEffect() != null) {    // Use Actor's Click Effect first
                        actor.obj().getClickEffect().produceTween(actor).start(tweenManager);
                    } else {
                        StatefulStage statefulStage = sceneToClickableListEntry.getKey().state().getAttachedStage();
                        if (!StatefulStage.isNull(statefulStage) && statefulStage.obj().getTextEffects().clickEffect != null) {    // Use Stage's click effect next
                            statefulStage.obj().getTextEffects().clickEffect.produceTween(actor).start(tweenManager);
                        } else if (textEffects.clickEffect != null) {   // Fallback to engine's click effect
                            textEffects.clickEffect.produceTween(actor).start(tweenManager);
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
