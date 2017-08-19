package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.injection.StorytellerInjector;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public final class Commons {

    @Inject
    public Commons() {

    }

    public StorytellerInjector injector;

    public BitmapFont font;
    public SpriteBatch batch;
    public Viewport viewport;
    public AmbientLight ambientLight;
    public ShaderProgram defaultShader;
    public Vector2 worldDimensions;
}
