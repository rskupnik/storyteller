package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StatefulStage;

import java.util.ArrayList;

/**
 * Holds all Stages used in the engine as StagePair objects.
 */
public final class Stages extends ArrayList<StatefulStage> {

    public StatefulStage find(String id) {
        for (StatefulStage s : this) {
            if (s.obj().getId().equals(id))
                return s;
        }
        return null;
    }
}
