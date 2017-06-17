package com.github.rskupnik.storyteller.effects.transformers;

/**
 * Transform the intermediate scene (TransformedScene) into a format that this particular effect
 * requires to work. For example, if we want text to appear line-by-line, then we need the text
 * to store line information.
 */
public interface EffectTransformer {

    Object getData();
    void transform();
}
