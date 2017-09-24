package com.github.rskupnik.storyteller.statefulobjects.objects;

import com.badlogic.gdx.graphics.Color;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;

import java.util.HashMap;
import java.util.Map;

public final class Actor {

    private String text;
    private Color color;
    private boolean clickable;
    private ClickEffect clickEffect;
    private Map<String, Object> data = new HashMap<>();

    public Actor(String text) {
        this.text = text;
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

    public ClickEffect getClickEffect() {
        return clickEffect;
    }

    public Map<String, Object> getData() {
        return data;
    }

    private void setColor(Color color) {
        this.color = color;
    }

    private void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    private void setClickEffect(ClickEffect clickEffect) {
        this.clickEffect = clickEffect;
    }

    private void setData(Map<String, Object> data) {
        this.data = data;
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

        public ActorBuilder withClickEffect(ClickEffect clickEffect) {
            actor.setClickEffect(clickEffect);
            return this;
        }

        public ActorBuilder withData(Map<String, Object> data) {
            actor.setData(data);
            return this;
        }

        public Actor build() {
            return actor;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Actor actor = (Actor) o;

        if (clickable != actor.clickable) return false;
        if (!text.equals(actor.text)) return false;
        if (color != null ? !color.equals(actor.color) : actor.color != null) return false;
        return clickEffect != null ? clickEffect.equals(actor.clickEffect) : actor.clickEffect == null;
    }

    @Override
    public int hashCode() {
        int result = text.hashCode();
        result = 31 * result + (color != null ? color.hashCode() : 0);
        result = 31 * result + (clickable ? 1 : 0);
        result = 31 * result + (clickEffect != null ? clickEffect.hashCode() : 0);
        return result;
    }
}
