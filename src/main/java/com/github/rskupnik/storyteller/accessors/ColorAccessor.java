package com.github.rskupnik.storyteller.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.badlogic.gdx.graphics.Color;

public class ColorAccessor implements TweenAccessor<Color> {

    public static final int ALPHA = 0;

    @Override
    public int getValues(Color target, int tweenType, float[] floats) {
        switch (tweenType) {
            case ALPHA: floats[0] = target.a; return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(Color target, int tweenType, float[] floats) {
        switch (tweenType) {
            case ALPHA: target.a = floats[0]; break;
            default: assert false; break;
        }
    }
}
