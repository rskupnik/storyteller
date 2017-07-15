package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * Holds all Scenes used in the engine as StatefulScene objects.
 */
@Singleton
public final class Scenes extends ArrayList<StatefulScene> {

    @Inject
    public Scenes() {

    }

    public StatefulScene find(String id) {
        for (StatefulScene sp : this) {
            if (sp.obj().getId().equals(id))
                return sp;
        }
        return null;
    }
}
