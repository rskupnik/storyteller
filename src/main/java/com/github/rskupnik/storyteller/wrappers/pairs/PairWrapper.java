package com.github.rskupnik.storyteller.wrappers.pairs;

import org.javatuples.Pair;

/**
 * This wrapper is used to give some better method names to javatuples' default Pair class
 * This class is intended to be extended with particular types, usually an Peripheral - Internal pair.
 */
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

    public boolean notNull() {
        return left() != null && right() != null;
    }
}
