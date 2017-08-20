package com.github.rskupnik.storyteller.structs.backgrounds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public final class BasicBackground extends Background {

    private Color postPublishColor;

    public BasicBackground(Texture image) {
        super(image);
    }

    public Color getPostPublishColor() {
        return postPublishColor;
    }

    public void setPostPublishColor(Color postPublishColor) {
        this.postPublishColor = postPublishColor;
    }
}
