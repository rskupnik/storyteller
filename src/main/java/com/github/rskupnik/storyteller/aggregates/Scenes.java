package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

import java.util.ArrayList;

/**
 * Holds all Scenes used in the engine as StatefulScene objects.
 */
public final class Scenes extends ArrayList<StatefulScene> {

    public StatefulScene find(String id) {
        for (StatefulScene sp : this) {
            if (sp.obj().getId().equals(id))
                return sp;
        }
        return null;
    }
}
