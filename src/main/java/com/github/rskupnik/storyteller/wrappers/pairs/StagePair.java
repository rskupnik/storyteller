package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.internals.InternalStage;
import com.github.rskupnik.storyteller.peripheral.Stage;

public final class StagePair extends PairWrapper<Stage, InternalStage> {

    public StagePair(Stage left, InternalStage right) {
        super(left, right);
    }

    public Stage stage() {
        return super.left();
    }

    public InternalStage internal() {
        return super.right();
    }

    @Override
    public int hashCode() {
        return left().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Stage)
            return left().equals(obj);
        else if (obj instanceof StagePair)
            return left().equals(((StagePair) obj).left());
        else return false;
    }
}
