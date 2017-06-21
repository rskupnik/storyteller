package com.github.rskupnik.storyteller.core.transformation;

public interface Transformer<I, O> {
    O transform(I input);
}
