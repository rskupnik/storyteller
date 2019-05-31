package com.github.rskupnik.storyteller;

import com.badlogic.gdx.InputProcessor;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.statefulobjects.objects.Scene;
import com.github.rskupnik.storyteller.listeners.EventListener;
import com.github.rskupnik.storyteller.statefulobjects.objects.Stage;

public interface TextEngine {
    void render(float delta);
    void resize(int width, int height);

    void setEventListener(EventListener eventListener);
    void setTextClickEffect(ClickEffect effect);

    void attachScene(String stageId, Scene scene);
    void removeScene(String id);
    void removeScene(Scene scene);

    void addStage(Stage stage);

    void setAmbientLight(AmbientLight light);
    void addLight(Light light);

    InputProcessor getInputProcessor();
}
