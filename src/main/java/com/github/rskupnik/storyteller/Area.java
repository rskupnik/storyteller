package com.github.rskupnik.storyteller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

final class Area {

    private Rectangle rectangle;
    private Vector2 topLeft;
    private int width;

    Area(Rectangle rectangle) {
        this.rectangle = rectangle;
        this.topLeft = new Vector2(rectangle.x, rectangle.y + rectangle.height);
        this.width = (int) rectangle.width;
    }

    Rectangle getRectangle() {
        return rectangle;
    }

    @Override
    public int hashCode() {
        return rectangle.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Area))
            return false;

        return rectangle.equals(((Area)obj).getRectangle());
    }
}
