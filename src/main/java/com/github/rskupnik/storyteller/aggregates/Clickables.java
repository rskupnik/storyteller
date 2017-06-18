package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Scene;

import java.util.HashMap;
import java.util.Map;

public class Clickables extends HashMap<Scene, Map<Rectangle, Actor>> {

    public void addClickable(Scene scene, Rectangle rectangle, Actor actor) {
        Map<Rectangle, Actor> innerMap = this.get(scene);
        if (innerMap == null)
            innerMap = new HashMap<>();
        innerMap.put(rectangle, actor);
        this.put(scene, innerMap);
    }

    public void clearClickables() {
        this.clear();
    }

    public void clearClickables(Scene scene) {
        this.get(scene).clear();
    }

    public void removeScene(Scene scene) {
        clearClickables(scene);
        this.remove(scene);
    }
}
