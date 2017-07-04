package com.github.rskupnik.storyteller.core.renderingunits;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

/**
 * Group of classes that decide how text will appear on the Stage and disappear from it.
 */
public abstract class RenderingUnit {

    private ExtenderChain chain;

    public abstract void init(RenderingUnitInitializer initializer);

    public abstract void render(float delta, ScenePair scenePair);

    protected void setChain(ExtenderChain chain) {
        this.chain = chain;
    }

    public ExtenderChain getChain() {
        return chain;
    }
}
