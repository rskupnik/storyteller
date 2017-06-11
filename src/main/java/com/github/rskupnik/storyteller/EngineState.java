package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

final class EngineState {

    EngineState() {
        listeners = new Listeners();
        effects = new Effects();
        tweenManager = new TweenManager();
    }

    TextEngine engine;
    InputHandler inputHandler;
    Renderer renderer;

    Listeners listeners;
    Effects effects;

    Scene currentScene;
    boolean firstSceneDraw;
    TweenManager tweenManager;

    final class Listeners {
        ClickListener clickListener;
    }

    final class Effects {
        TextEffect textClickEffect;
    }
}
