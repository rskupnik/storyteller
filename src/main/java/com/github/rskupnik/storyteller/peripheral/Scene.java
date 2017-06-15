package com.github.rskupnik.storyteller.peripheral;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Scene {

    private String id, areaId;
    private final List<Actor> actors = new LinkedList<Actor>();

    private Scene(String id, String areaId) {
        this.id = id;
        this.areaId = areaId;
    }

    private void addActor(Actor actor) {
        actors.add(actor);
    }

    public List<Actor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public String getAreaId() {
        return areaId;
    }

    public String getId() {
        return id;
    }

    public static SceneBuilder newScene(String id, String areaId) {
        return new SceneBuilder(id, areaId);
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

        private SceneBuilder(String id, String areaId) {
            scene = new Scene(id, areaId);
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
