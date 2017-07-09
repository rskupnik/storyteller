package com.github.rskupnik.storyteller.accessors;

import aurelienribon.tweenengine.TweenAccessor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;

public class ActorAccessor implements TweenAccessor<StatefulActor> {

    public static final int POSITION_Y = 0;

    @Override
    public int getValues(StatefulActor target, int tweenType, float[] floats) {
        switch (tweenType) {
            case POSITION_Y: floats[0] = target.state().getYOffset(); return 1;
            default: assert false; return -1;
        }
    }

    @Override
    public void setValues(StatefulActor target, int tweenType, float[] floats) {
        switch (tweenType) {
            case POSITION_Y: target.state().setYOffset(floats[0]); break;
            default: assert false; break;
        }
    }
}
