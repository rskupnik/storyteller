package com.github.rskupnik.storyteller.effects.click;

import aurelienribon.tweenengine.Tween;
import com.github.rskupnik.storyteller.peripheral.internals.InternalActor;

public abstract class ClickEffect {

    protected int effectType;
    protected float duration;
    protected float[] target;

    ClickEffect(int type, float duration, float[] target) {
        this.effectType = type;
        this.duration = duration;
        this.target = target;
    }

    public Tween produceTween(InternalActor actor) {
        return Tween.to(actor, effectType, duration).target(target);
    }
}
