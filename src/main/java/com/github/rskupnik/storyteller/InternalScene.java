package com.github.rskupnik.storyteller;

public final class InternalScene {

    private boolean firstDraw = true;

    public boolean isFirstDraw() {
        return firstDraw;
    }

    public void wasDrawn() {
        firstDraw = false;
    }
}
