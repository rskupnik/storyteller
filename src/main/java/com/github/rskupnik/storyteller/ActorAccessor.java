package com.github.rskupnik.storyteller;

import aurelienribon.tweenengine.TweenAccessor;

public class ActorAccessor implements TweenAccessor<InternalActor> {

    public static final int POSITION_Y = 0;

    @Override
    public int getValues(InternalActor target, int tweenType, float[] floats) {
        switch (tweenType) {
            case POSITION_Y: floats[0] = target.getYOffset(); return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(InternalActor target, int tweenType, float[] floats) {
        switch (tweenType) {
            case POSITION_Y: target.setyOffset(floats[0]); break;
            default: assert false; break;
        }
    }
}
