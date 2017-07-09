package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.peripheral.internals.SceneState;
import com.github.rskupnik.storyteller.structs.StatefulObject;

public class StatefulScene extends StatefulObject<Scene, SceneState> {

    public StatefulScene(Scene object, SceneState state) {
        super(object, state);
    }
}
