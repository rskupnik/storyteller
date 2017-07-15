package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.injection.DaggerStorytellerInjector;
import com.github.rskupnik.storyteller.injection.EngineModule;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;

public final class Storyteller {

    private TextEngine engine;

    private Storyteller(TextEngine engine) {
        this.engine = engine;
    }

    public static Storyteller newEngine(Stage stage, BitmapFont font) {
        /**StorytellerInjector injector = Guice.createInjector(new EngineModule());
        TextEngine engine = injector.getInstance(TextEngine.class);
        ((TextEngineImpl) engine).init(injector, stage, font);
        return new Storyteller(engine);**/

        StorytellerInjector injector = DaggerStorytellerInjector.create();
        TextEngine engine = injector.engine();
        ((TextEngineImpl) engine).init(injector, stage, font);

        return new Storyteller(engine);
    }

    public Storyteller withStage(Stage stage) {
        engine.addStage(stage);
        return this;
    }

    public Storyteller withClickListener(ClickListener clickListener) {
        engine.setClickListener(clickListener);
        return this;
    }

    public Storyteller withTextClickEffect(ClickEffect effect) {
        engine.setTextClickEffect(effect);
        return this;
    }

    public TextEngine build() {
        return engine;
    }
}
