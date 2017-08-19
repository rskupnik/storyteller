package com.github.rskupnik.storyteller.core.rendering;

import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

public abstract class RenderingUnit {

    private boolean firstRender = true;

    public void render(float delta, StatefulScene scene) {
        if (firstRender) {
            firstRender = false;
            preFirstRender(scene);
        }
    }

    public abstract void preFirstRender(StatefulScene scene);
}
