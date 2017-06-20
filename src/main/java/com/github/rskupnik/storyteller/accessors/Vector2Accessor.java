package com.github.rskupnik.storyteller.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.math.Vector2;

public class Vector2Accessor implements TweenAccessor<Vector2> {

    public static final int Y = 0;

    @Override
    public int getValues(Vector2 target, int tweenType, float[] floats) {
        switch (tweenType) {
            case Y: floats[0] = target.y; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Vector2 target, int tweenType, float[] floats) {
        switch (tweenType) {
            case Y: target.y = floats[0]; break;
            default: assert false; break;
        }
    }
}
