package com.github.rskupnik.storyteller.structs.backgrounds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;

public abstract class Background {

    private Texture image;
    private Color tint = new Color(1, 1, 1, 1);
    private boolean exitSequenceStarted, exitSequenceFinished;

    public Background(Texture image) {
        this.image = image;
    }

    public Texture getImage() {
        return image;
    }

    public boolean isExitSequenceStarted() {
        return exitSequenceStarted;
    }

    public void setExitSequenceStarted(boolean exitSequenceStarted) {
        this.exitSequenceStarted = exitSequenceStarted;
    }

    public boolean isExitSequenceFinished() {
        return exitSequenceFinished;
    }

    public void setExitSequenceFinished(boolean exitSequenceFinished) {
        this.exitSequenceFinished = exitSequenceFinished;
    }

    public Color getTint() {
        return tint;
    }

    public void setTint(Color tint) {
        this.tint = tint;
    }
}
