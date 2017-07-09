package com.github.rskupnik.storyteller.statefulobjects;

import com.github.rskupnik.storyteller.statefulobjects.objects.Scene;
import com.github.rskupnik.storyteller.statefulobjects.states.SceneState;
import com.github.rskupnik.storyteller.structs.StatefulObject;

public class StatefulScene extends StatefulObject<Scene, SceneState> {

    public StatefulScene(Scene object, SceneState state) {
        super(object, state);
    }
}
