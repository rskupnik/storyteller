package com.github.rskupnik.storyteller.core.transformation;

public abstract class ChainedTransformer<I, O> implements Transformer<I, O> {

    private ChainedTransformer next;

    public void setNext(ChainedTransformer next) {
        this.next = next;
    }

    public ChainedTransformer next() {
        return next;
    }
}
