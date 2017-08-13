package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

@Singleton
public final class SceneSwaps extends HashMap<StatefulStage, StatefulScene> {

    @Inject
    public SceneSwaps() {

    }
}
