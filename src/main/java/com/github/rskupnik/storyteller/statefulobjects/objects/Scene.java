package com.github.rskupnik.storyteller.statefulobjects.objects;

import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.states.ActorState;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Scene {

    private String id;
    private final List<StatefulActor> actors = new LinkedList<>();

    private boolean dirty = true;

    private Scene(String id) {
        this.id = id;
    }

    private void addActor(Actor actor) {
        actors.add(new StatefulActor(actor, new ActorState()));
    }

    public void append(Actor actor) {
        actors.add(new StatefulActor(actor, new ActorState()));
        dirty = true;
    }

    public void appendLine(Actor actor) {
        actors.add(new StatefulActor(Actor.newActor("\n").build(), new ActorState()));
        actors.add(new StatefulActor(actor, new ActorState()));
        dirty = true;
    }

    public List<StatefulActor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public String getId() {
        return id;
    }

    public void setDirty(boolean b) {
        this.dirty = b;
    }

    public boolean isDirty() {
        return dirty;
    }

    public static SceneBuilder newScene(String id) {
        return new SceneBuilder(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        return ((Scene) o).id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public static final class SceneBuilder {

        private final Scene scene;  // TODO: Use Private Data Class design pattern?

        private boolean autoSpace;

        private SceneBuilder(String id) {
            scene = new Scene(id);
        }

        public Scene build() {
            return scene;
        }

        public SceneBuilder text(Actor actor) {
            scene.addActor(actor);
            if (autoSpace)
                space();
            return this;
        }

        public SceneBuilder autoSpace() {
            this.autoSpace = !this.autoSpace;
            return this;
        }

        public SceneBuilder space() {
            scene.addActor(Actor.newActor(" ").build());
            return this;
        }

        public SceneBuilder newLine() {
            scene.addActor(Actor.newActor("\n").build());
            return this;
        }
    }
}
