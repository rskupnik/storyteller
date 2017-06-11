package com.github.rskupnik.storyteller.effects;

import aurelienribon.tweenengine.Tween;
import com.github.rskupnik.storyteller.Actor;
import com.github.rskupnik.storyteller.InternalActor;

public abstract class TextEffect {

    protected int effectType;
    protected float duration;
    protected float[] target;

    TextEffect(int type, float duration, float[] target) {
        this.effectType = type;
        this.duration = duration;
        this.target = target;
    }

    public Tween produceTween(InternalActor actor) {
        return Tween.to(actor, effectType, duration).target(target);
    }

    public int getEffectType() {
        return effectType;
    }

    public float getDuration() {
        return duration;
    }

    public float[] getTarget() {
        return target;
    }
}
