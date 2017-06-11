package com.github.rskupnik.storyteller;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Scene {
    private final List<Actor> actors = new LinkedList<Actor>();

    private Scene() {

    }

    private void addActor(Actor actor) {
        actors.add(actor);
    }

    List<Actor> getActors() {
        return Collections.unmodifiableList(actors);
    }

    public static SceneBuilder newScene() {
        return new SceneBuilder();
    }

    public static final class SceneBuilder {

        private final Scene scene;  // TODO: Use Private Data Class design pattern?

        private SceneBuilder() {
            scene = new Scene();
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
