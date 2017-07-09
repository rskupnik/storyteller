package com.github.rskupnik.storyteller.statefulobjects;

import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;
import com.github.rskupnik.storyteller.statefulobjects.states.StageState;
import com.github.rskupnik.storyteller.structs.StatefulObject;

public class StatefulStage extends StatefulObject<Stage, StageState> {

    public StatefulStage(Stage object, StageState state) {
        super(object, state);
    }
}
