package com.github.rskupnik.storyteller.peripheral;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public final class Scene {

    private String id;
    private final List<Actor> actors = new LinkedList<Actor>();

    private Scene(String id) {
        this.id = id;
    }

    private void addActor(Actor actor) {
        actors.add(actor);
    }

    public List<Actor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public String getId() {
        return id;
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

        private SceneBuilder(String id) {
            scene = new Scene(id);
        }

        public Scene build() {
            return scene;
        }

        public SceneBuilder text(Actor actor) {
            scene.addActor(actor);
            return this;
        }

        public SceneBuilder space() {
            scene.addActor(Actor.newActor(" ").build());
            return this;
        }
    }
}
