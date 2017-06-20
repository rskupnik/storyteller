package com.github.rskupnik.storyteller;

import com.badlogic.gdx.InputProcessor;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.listeners.ClickListener;
import com.github.rskupnik.storyteller.peripheral.Stage;

public interface TextEngine {
    void render(float delta);
    void resize(int width, int height);

    void setClickListener(ClickListener clickListener);
    void setTextClickEffect(ClickEffect effect);

    void attachScene(String stageId, Scene scene);
    void removeScene(String id);
    void removeScene(Scene scene);

    void addStage(Stage stage);

    InputProcessor getInputProcessor();
}
