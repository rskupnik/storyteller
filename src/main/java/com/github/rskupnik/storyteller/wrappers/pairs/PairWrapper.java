package com.github.rskupnik.storyteller.wrappers.pairs;

import org.javatuples.Pair;

public abstract class PairWrapper<L, R> {

    private Pair<L, R> object;

    public PairWrapper(L left, R right) {
        this.object = org.javatuples.Pair.with(left, right);
    }

    public Pair<L, R> obj() {
        return object;
    }

    public L left() {
        return object.getValue0();
    }

    public R right() {
        return object.getValue1();
    }
}
