package com.github.rskupnik.storyteller.effects;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.equations.Quad;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.peripheral.internals.InternalActor;

public class VerticalMoveTextEffect extends TextEffect {

    private TweenEquation equation = Quad.IN;

    public VerticalMoveTextEffect(float duration, float target) {
        super(ActorAccessor.POSITION_Y, duration, new float[] {target});
    }

    public VerticalMoveTextEffect withEquation(TweenEquation equation) {
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
