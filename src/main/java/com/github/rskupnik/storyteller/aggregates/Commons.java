package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.github.rskupnik.storyteller.effects.appear.TypewriterAppearEffect;
import com.github.rskupnik.storyteller.effects.transformers.EffectTransformer;
import com.github.rskupnik.storyteller.effects.appear.AppearEffect;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import org.javatuples.Pair;

public final class Commons {

    public BitmapFont font;
    public TransformedScene transformedScene;   // TODO: This is only temporarily here
    public AppearEffect appearEffect = new TypewriterAppearEffect();   // TODO: Temp here, should be bound to Scene/Stage
}
