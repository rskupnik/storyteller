package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.Gdx;
import com.github.rskupnik.storyteller.core.lighting.Light;

public class LightUtils {

    public static void updateLightToMousePosition(Light light) {
        float x = (float) Gdx.input.getX() / (float) Gdx.graphics.getWidth();
        float y = ((float) Gdx.graphics.getHeight() - (float) Gdx.input.getY()) / (float) Gdx.graphics.getHeight();

        light.setPosition(x, y);
    }
}
