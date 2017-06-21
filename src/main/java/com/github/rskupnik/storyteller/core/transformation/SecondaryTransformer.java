package com.github.rskupnik.storyteller.core.transformation;

import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;

public interface SecondaryTransformer<I, O> {
    O transform(TransformedScene<I> input);
}
