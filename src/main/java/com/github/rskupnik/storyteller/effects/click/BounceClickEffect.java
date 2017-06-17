package com.github.rskupnik.storyteller.effects.click;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Quad;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.peripheral.internals.InternalActor;

public final class BounceClickEffect extends ClickEffect {

    private TweenEquation equation = Quad.IN;

    public BounceClickEffect(float duration, float target) {
        super(ActorAccessor.POSITION_Y, duration, new float[] {target});
    }

    public BounceClickEffect withEquation(TweenEquation equation) {
        this.equation = equation;
        return this;
    }

    @Override
    public Tween produceTween(InternalActor actor) {
        return super.produceTween(actor)
                .ease(equation)
                .repeatYoyo(1, 0);
    }
}
