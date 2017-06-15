package com.github.rskupnik.storyteller;

import com.badlogic.gdx.InputProcessor;
import com.github.rskupnik.storyteller.effects.TextEffect;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.listeners.ClickListener;

public interface TextEngine {
    void render(float delta);
    void resize(int width, int height);

    void setClickListener(ClickListener clickListener);
    void setTextClickEffect(TextEffect effect);

    void addScene(Scene scene);
    void removeScene(String id);
    void removeScene(Scene scene);

    InputProcessor getInputProcessor();
}
