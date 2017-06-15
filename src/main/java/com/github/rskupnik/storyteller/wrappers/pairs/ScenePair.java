package com.github.rskupnik.storyteller.wrappers.pairs;

import com.github.rskupnik.storyteller.peripheral.InternalScene;
import com.github.rskupnik.storyteller.peripheral.Scene;

public final class ScenePair extends PairWrapper<Scene, InternalScene> {

    public ScenePair(Scene left, InternalScene right) {
        super(left, right);
    }
}
