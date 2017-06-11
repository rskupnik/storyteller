package com.github.rskupnik.storyteller;

import com.badlogic.gdx.InputProcessor;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.listeners.ClickListener;

public interface TextEngine {
    void render(float delta);
    void resize(int width, int height);

    void setClickListener(ClickListener clickListener);
    void setTextClickEffect(TextEffect effect);

    void setScene(Scene scene);

    InputProcessor getInputProcessor();
}
