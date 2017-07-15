package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * Holds all Stages used in the engine as StagePair objects.
 */
@Singleton
public final class Stages extends ArrayList<StatefulStage> {

    @Inject
    public Stages() {

    }

    public StatefulStage find(String id) {
        for (StatefulStage s : this) {
            if (s.obj().getId().equals(id))
                return s;
        }
        return null;
    }
}
