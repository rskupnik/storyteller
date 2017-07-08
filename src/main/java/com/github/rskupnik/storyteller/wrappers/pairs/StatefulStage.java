package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.peripheral.internals.StageState;
import com.github.rskupnik.storyteller.structs.StatefulObject;

public class StatefulStage extends StatefulObject<Stage, StageState> {

    public StatefulStage(Stage object, StageState state) {
        super(object, state);
    }
}
