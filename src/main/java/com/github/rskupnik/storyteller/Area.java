package com.github.rskupnik.storyteller;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

final class Area {

    private String id;
    private Rectangle rectangle;
    private Vector2 topLeft;
    private int width;

    Area(String id, Rectangle rectangle) {
        this.id = id;
        this.rectangle = rectangle;
        this.topLeft = new Vector2(rectangle.x, rectangle.y + rectangle.height);
        this.width = (int) rectangle.width;
    }

    String getId() {
        return id;
    }

    Rectangle getRectangle() {
        return rectangle;
    }

    Vector2 getTopLeft() {
        return topLeft;
    }

    int getWidth() {
        return width;
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

final class AreaUtils {

    /**
     * Calculates the remaining width available to fill with text in the current text row.
     */
    static int calcRemainingWidth(Area area, int currentX) {
        return area.getWidth() - (currentX - (int) area.getTopLeft().x);
    }

    /**
     * Checks whether x points to the beginning of a new text line or somewhere in an existing one.
     */
    static boolean notStartOfLine(Area area, int x) {
        return x != (int) area.getTopLeft().x;
    }
}