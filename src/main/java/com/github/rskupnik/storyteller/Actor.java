package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.Color;
import com.github.rskupnik.storyteller.effects.TextEffect;

public class Actor {

    private String text;
    private Color color;
    private boolean clickable;
    private TextEffect clickEffect;

    private InternalActor internalActor = new InternalActor();

    Actor(String text) {
        this.text = text;
    }

    InternalActor getInternalActor() {
        return internalActor;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }

    public boolean isClickable() {
        return clickable;
    }

    public TextEffect getClickEffect() {
        return clickEffect;
    }

    private void setColor(Color color) {
        this.color = color;
    }

    private void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    private void setClickEffect(TextEffect clickEffect) {
        this.clickEffect = clickEffect;
    }

    public static ActorBuilder newActor(String text) {
        return new ActorBuilder(new Actor(text));
    }

    public static class ActorBuilder {

        private Actor actor;

        private ActorBuilder(Actor actor) {
            this.actor = actor;
        }

        public ActorBuilder color(Color color) {
            actor.setColor(color);
            return this;
        }

        public ActorBuilder clickable() {
            actor.setClickable(true);
            return this;
        }

        public ActorBuilder withClickEffect(TextEffect clickEffect) {
            actor.setClickEffect(clickEffect);
            return this;
        }

        public Actor build() {
            return actor;
        }
    }
}
