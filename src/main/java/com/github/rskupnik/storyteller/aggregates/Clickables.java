package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.structs.Clickable;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Singleton
public class Clickables extends HashMap<StatefulScene, List<Clickable>> {

    @Inject
    public Clickables() {

    }

    public void addClickable(StatefulScene scene, Rectangle rectangle, StatefulActor actor, GlyphLayout GL) {
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
        List<Clickable> clickablesPerScene = this.get(StatefulScene);
        if (clickablesPerScene == null)
            return;

        for (Iterator<Clickable> it = clickablesPerScene.iterator(); it.hasNext();) {
            Clickable clickable = it.next();
            if (clickable.rectangle().equals(rectangle)) {
                it.remove();
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
