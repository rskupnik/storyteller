package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.structs.State;

import static com.github.rskupnik.storyteller.statefulobjects.states.StageState.ID.ATTACHED_SCENE;

public final class StageState extends State<StageState.ID> {

    protected enum ID {
        ATTACHED_SCENE
    }

    public void attachScene(StatefulScene scene) {
        put(ATTACHED_SCENE, scene);
    }

    public StatefulScene getAttachedScene() {
        return (StatefulScene) get(ATTACHED_SCENE);
    }
}
