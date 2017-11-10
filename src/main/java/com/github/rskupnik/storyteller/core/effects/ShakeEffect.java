package com.github.rskupnik.storyteller.core.effects;

public final class ShakeEffect extends StageEffect {

    private final float strength;
    private final int duration;
    private final int amount;
    private final int interval;

    public ShakeEffect(float strength, int duration, int amount, int interval) {
        this.strength = strength;
        this.duration = duration;
        this.amount = amount;
        this.interval = interval;
    }

    public float getStrength() {
        return strength;
    }

    public int getDuration() {
        return duration;
    }

    public int getAmount() {
        return amount;
    }

    public int getInterval() {
        return interval;
    }
}
