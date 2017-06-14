package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

import java.util.HashMap;
import java.util.Map;

public final class EngineState {

    public EngineState() {
        effects = new Effects();
        tweenManager = new TweenManager();
    }

    TextEngine engine;
    InputHandler inputHandler;

    Effects effects;

    Map<String, Scene> scenes = new HashMap<>();
    TweenManager tweenManager;

    final class Effects {
        TextEffect textClickEffect;
    }
}
