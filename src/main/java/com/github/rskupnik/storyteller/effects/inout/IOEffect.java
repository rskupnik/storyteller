package com.github.rskupnik.storyteller.effects.inout;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;

/**
 * Group of classes that decide how text will appear on the Stage and disappear from it.
 */
public abstract class IOEffect {

    private ExtenderChain chain;

    public IOEffect(ExtenderChain chain) {
        this.chain = chain;
    }

    public abstract void render(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, ScenePair scenePair);

    public ExtenderChain getChain() {
        return chain;
    }
}
