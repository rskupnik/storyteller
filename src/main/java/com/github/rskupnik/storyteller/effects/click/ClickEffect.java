package com.github.rskupnik.storyteller.effects.click;

import aurelienribon.tweenengine.Tween;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;

public abstract class ClickEffect {

    protected int effectType;
    protected float duration;
    protected float[] target;

    ClickEffect(int type, float duration, float[] target) {
        this.effectType = type;
        this.duration = duration;
        this.target = target;
    }

    public Tween produceTween(StatefulActor actor) {
        return Tween.to(actor, effectType, duration).target(target);
    }
}
