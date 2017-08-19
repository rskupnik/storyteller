package com.github.rskupnik.storyteller.structs.backgrounds;

import com.badlogic.gdx.graphics.Texture;

public abstract class Background {

    private Texture image;

    public Background(Texture image) {
        this.image = image;
    }

    public Texture getImage() {
        return image;
    }
}
