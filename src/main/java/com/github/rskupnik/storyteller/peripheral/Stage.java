package com.github.rskupnik.storyteller.peripheral;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.effects.inout.IOEffect;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;

public final class Stage {

    private String id;
    private Rectangle rectangle;

    private IOEffect IOEffect;
    private TextEffects textEffects = new TextEffects();    // TextEffects can be defined for the whole engine or for a single Stage

    private Vector2 topLeft;
    private int width;

    private Stage(String id, Rectangle rectangle) {
        this.id = id;
        this.rectangle = rectangle;
        this.topLeft = new Vector2(rectangle.x, rectangle.y + rectangle.height);
        this.width = (int) rectangle.width;
    }

    public String getId() {
        return id;
    }

    public Rectangle getRectangle() {
        return rectangle;
    }

    public Vector2 getTopLeft() {
        return topLeft;
    }

    public int getWidth() {
        return width;
    }

    public IOEffect getIOEffect() {
        return IOEffect;
    }

    private void setIOEffect(IOEffect IOEffect) {
        this.IOEffect = IOEffect;
    }

    public TextEffects getTextEffects() {
        return textEffects;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Stage))
            return false;

        return id.equals(((Stage)obj).getId());
    }

    public static StageBuilder newStage(String id, Vector2 bottomLeft, Vector2 dimensions) {
        return new StageBuilder(id, bottomLeft, dimensions);
    }

    public static final class StageBuilder {

        private Stage stage;

        public StageBuilder(String id, Vector2 bottomLeft, Vector2 dimensions) {
            this.stage = new Stage(id, new Rectangle(bottomLeft.x, bottomLeft.y, dimensions.x, dimensions.y));
        }

        public StageBuilder withAppearEffect(IOEffect IOEffect) {
            stage.setIOEffect(IOEffect);
            return this;
        }

        public StageBuilder withClickEffect(ClickEffect clickEffect) {
            stage.getTextEffects().clickEffect = clickEffect;
            return this;
        }

        public Stage build() {
            return stage;
        }
    }
}
