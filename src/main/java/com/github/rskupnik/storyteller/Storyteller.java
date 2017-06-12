package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

public final class Storyteller {

    private TextEngine engine;

    private Storyteller(TextEngine engine) {
        this.engine = engine;
    }

    public static Storyteller newEngine(String areaId, Rectangle area, BitmapFont font) {
        return new Storyteller(new TextEngineImpl(areaId, area, font));
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
