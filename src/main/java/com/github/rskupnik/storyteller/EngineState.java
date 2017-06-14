package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

import java.util.HashMap;
import java.util.Map;

public final class EngineState {

    public EngineState() {
        tweenManager = new TweenManager();
    }

    TextEngine engine;

    Map<String, Scene> scenes = new HashMap<>();
    TweenManager tweenManager;
}
