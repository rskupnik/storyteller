package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.injection.EngineModule;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.Stage;
import com.google.inject.Guice;
import com.google.inject.Injector;

public final class Storyteller {

    private TextEngine engine;

    private Storyteller(TextEngine engine) {
        this.engine = engine;
    }

    public static Storyteller newEngine(Stage stage, BitmapFont font) {
        Injector injector = Guice.createInjector(new EngineModule());
        TextEngine engine = injector.getInstance(TextEngine.class);
        ((TextEngineImpl) engine).init(injector, stage, font);
        return new Storyteller(engine);
    }

    public Storyteller withClickListener(ClickListener clickListener) {
        engine.setClickListener(clickListener);
        return this;
    }

    public Storyteller withTextClickEffect(TextEffect effect) {
        engine.setTextClickEffect(effect);
        return this;
    }

    public TextEngine build() {
        return engine;
    }
}
