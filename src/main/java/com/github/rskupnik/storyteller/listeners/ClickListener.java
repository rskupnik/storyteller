package com.github.rskupnik.storyteller.listeners;

import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.Actor;

public interface ClickListener {
    void onActorClicked(Actor actor, Vector2 point, int button);
}
