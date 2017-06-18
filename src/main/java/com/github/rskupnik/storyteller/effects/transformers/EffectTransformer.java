package com.github.rskupnik.storyteller.effects.transformers;

import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;

/**
 * Transform the intermediate scene (TransformedScene) into a format that this particular effect
 * requires to work. For example, if we want text to appear line-by-line, then we need the text
 * to store line information.
 *
 * Usage: transform() should be called once on a TransformedScene and the result should be stored
 * inside the implementing class. Then, getData() should return the transformed data.
 */
public interface EffectTransformer {

    Object getData();
    void transform(TransformedScene input);
}
