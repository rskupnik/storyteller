package com.github.rskupnik.storyteller.statefulobjects.objects;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.aggregates.TextEffects;
import com.github.rskupnik.storyteller.effects.click.ClickEffect;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.textrenderer.TextRenderer;

public final class Stage {

    private String id;
    private Rectangle rectangle;
    private Background background;
    private Rectangle backgroundArea;
    private TextRenderer textRenderer;
    private TextEffects textEffects = new TextEffects();    // TextEffects can be defined for the whole engine or for a single Stage
    private boolean newBackground;

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

    public TextEffects getTextEffects() {
        return textEffects;
    }

    public Background getBackground() {
        return background;
    }

    public void setBackground(Background background) {
        this.background = background;
        setNewBackground(true);
    }

    public Rectangle getBackgroundArea() {
        return backgroundArea;
    }

    public void setBackgroundArea(Rectangle backgroundArea) {
        this.backgroundArea = backgroundArea;
    }

    public TextRenderer getTextRenderer() {
        return textRenderer;
    }

    public void setTextRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    public boolean isNewBackground() {
        return newBackground;
    }

    public void setNewBackground(boolean newBackground) {
        this.newBackground = newBackground;
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

        public StageBuilder withTextRenderer(TextRenderer textRenderer) {
            stage.setTextRenderer(textRenderer);
            return this;
        }

        public StageBuilder withClickEffect(ClickEffect clickEffect) {
            stage.getTextEffects().clickEffect = clickEffect;
            return this;
        }

        public StageBuilder withBackground(Background background) {
            stage.setBackground(background);
            return this;
        }

        public StageBuilder withBackground(Background background, Rectangle area) {
            stage.setBackground(background);
            stage.setBackgroundArea(area);
            return this;
        }

        public Stage build() {
            return stage;
        }
    }
}
