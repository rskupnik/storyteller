package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.peripheral.Stage;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;

import java.util.ArrayList;

public final class Stages extends ArrayList<StagePair> {

    public StagePair find(String id) {
        for (StagePair s : this) {
            if (s.stage().getId().equals(id))
                return s;
        }
        return null;
    }
}
