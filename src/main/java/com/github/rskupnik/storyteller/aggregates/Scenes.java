package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

import java.util.ArrayList;

/**
 * Holds all Scenes used in the engine as ScenePair objects.
 */
public final class Scenes extends ArrayList<ScenePair> {

    public ScenePair find(String id) {
        for (ScenePair sp : this) {
            if (sp.scene().getId().equals(id))
                return sp;
        }
        return null;
    }
}
