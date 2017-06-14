package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

import java.util.HashMap;
import java.util.Map;

public final class EngineState {

    public EngineState() {
        listeners = new Listeners();
        effects = new Effects();
        tweenManager = new TweenManager();
    }

    TextEngine engine;
    InputHandler inputHandler;

    Listeners listeners;
    Effects effects;

    Map<String, Scene> scenes = new HashMap<>();
    TweenManager tweenManager;

    final class Listeners {
        ClickListener clickListener;
    }

    final class Effects {
        TextEffect textClickEffect;
    }
}
