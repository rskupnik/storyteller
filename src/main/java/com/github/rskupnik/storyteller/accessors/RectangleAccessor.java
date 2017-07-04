package com.github.rskupnik.storyteller.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Rectangle;

public class RectangleAccessor implements TweenAccessor<Rectangle> {

    public static final int Y = 0;

    @Override
    public int getValues(Rectangle target, int tweenType, float[] floats) {
        switch (tweenType) {
            case Y: floats[0] = target.y; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Rectangle target, int tweenType, float[] floats) {
        switch (tweenType) {
            case Y: target.y = floats[0]; break;
            default: assert false; break;
        }
    }
}
