package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.structs.Clickable;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Clickables extends HashMap<ScenePair, List<Clickable>> {

    public void addClickable(ScenePair scene, Rectangle rectangle, Actor actor) {
        List<Clickable> clickablesPerScene = this.get(scene);
        if (clickablesPerScene == null)
            clickablesPerScene = new ArrayList<>();
        clickablesPerScene.add(new Clickable(rectangle, actor));
        this.put(scene, clickablesPerScene);
    }

    public void clearClickables() {
        this.clear();
    }

    public void clearClickables(ScenePair scene) {
        this.get(scene).clear();
    }

    public void removeScene(ScenePair scene) {
        clearClickables(scene);
        this.remove(scene);
    }
}
