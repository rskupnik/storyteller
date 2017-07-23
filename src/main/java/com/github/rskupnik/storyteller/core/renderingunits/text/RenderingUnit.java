package com.github.rskupnik.storyteller.core.renderingunits.text;

import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;

/**
 * Group of classes that decide how text will appear on the Stage and disappear from it.
 */
public abstract class RenderingUnit {

    private ExtenderChain chain;
    private boolean firstRender = true;

    public abstract void init(RenderingUnitInitializer initializer);

    public void render(float delta, StatefulScene scenePair) {
        if (firstRender) {
            firstRender = false;
            preFirstRender(scenePair);
        }
    }

    public abstract void preFirstRender(StatefulScene statefulScene);

    protected void setChain(ExtenderChain chain) {
        this.chain = chain;
    }

    public ExtenderChain getChain() {
        return chain;
    }
}
