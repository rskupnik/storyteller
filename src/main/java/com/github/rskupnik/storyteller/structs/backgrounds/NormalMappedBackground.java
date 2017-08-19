package com.github.rskupnik.storyteller.structs.backgrounds;

import com.badlogic.gdx.graphics.Texture;

public final class NormalMappedBackground extends Background {

    private Texture normals;

    private boolean initialized = false;

    public NormalMappedBackground(Texture image, Texture normals) {
        super(image);
        this.normals = normals;
    }

    public Texture getNormals() {
        return normals;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public void setInitialized(boolean initialized) {
        this.initialized = initialized;
    }
}
