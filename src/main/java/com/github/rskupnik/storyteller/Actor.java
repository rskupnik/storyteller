package com.github.rskupnik.storyteller;

import com.badlogic.gdx.graphics.Color;

public class Actor {

    private String text;
    private Color color;
    private boolean clickable;

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

    private void setColor(Color color) {
        this.color = color;
    }

    boolean isClickable() {
        return clickable;
    }

    private void setClickable(boolean clickable) {
        this.clickable = clickable;
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

        public Actor build() {
            return actor;
        }
    }
}
