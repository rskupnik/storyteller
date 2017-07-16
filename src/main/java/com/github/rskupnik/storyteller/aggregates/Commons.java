package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.Viewport;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class Commons {

    @Inject
    public Commons() {

    }

    public BitmapFont font;
    public SpriteBatch batch;
    public Viewport viewport;
}
