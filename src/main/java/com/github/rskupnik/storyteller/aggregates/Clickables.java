package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.structs.Clickable;
import com.github.rskupnik.storyteller.wrappers.pairs.StatefulScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Clickables extends HashMap<StatefulScene, List<Clickable>> {

    public void addClickable(StatefulScene scene, Rectangle rectangle, Actor actor, GlyphLayout GL) {
        List<Clickable> clickablesPerScene = this.get(scene);
        if (clickablesPerScene == null)
            clickablesPerScene = new ArrayList<>();
        clickablesPerScene.add(new Clickable(rectangle, actor, GL));
        this.put(scene, clickablesPerScene);
    }

    public void removeClickable(StatefulScene StatefulScene, Clickable target) {
        List<Clickable> clickablesPerScene = this.get(StatefulScene);
        if (clickablesPerScene == null)
            return;

        for (Iterator<Clickable> it = clickablesPerScene.iterator(); it.hasNext();) {
            Clickable clickable = it.next();
            if (clickable.actor().equals(target.actor()) && clickable.rectangle().equals(target.rectangle()))
                it.remove();
        }
    }

    public void removeRectangle(StatefulScene StatefulScene, Rectangle rectangle) {
        System.out.println("removeRectangle called");
        List<Clickable> clickablesPerScene = this.get(StatefulScene);
        if (clickablesPerScene == null)
            return;

        for (Iterator<Clickable> it = clickablesPerScene.iterator(); it.hasNext();) {
            Clickable clickable = it.next();
            if (clickable.rectangle().equals(rectangle)) {
                it.remove();
                System.out.println("Removed rectangle: "+rectangle);
            }
        }
    }

    public void clearClickables() {
        this.clear();
    }

    public void clearClickables(StatefulScene scene) {
        List<Clickable> clickables = this.get(scene);
        if (clickables != null)
            clickables.clear();
    }

    public void removeScene(StatefulScene scene) {
        clearClickables(scene);
        this.remove(scene);
    }
}
