package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.structs.State;

import static com.github.rskupnik.storyteller.statefulobjects.states.ActorState.ID.*;

public class ActorState extends State<ActorState.ID> {

    protected enum ID {
        Y_OFFSET,
        TRANSFORMED,
        EXTENDED,
        PROCESSED
    }

    public ActorState() {
        put(Y_OFFSET, 0f);
        put(TRANSFORMED, false);
        put(EXTENDED, false);
        put(PROCESSED, false);
    }

    public float getYOffset() {
        return (float) get(Y_OFFSET);
    }

    public void setYOffset(float yOffset) {
        put(Y_OFFSET, yOffset);
    }

    public boolean isProcessed() {
        return (boolean) get(PROCESSED);
    }

    public void setProcessed(boolean processed) {
        put(PROCESSED, processed);
    }

    public boolean isTransformed() {
        return (boolean) get(TRANSFORMED);
    }

    public void setTransformed(boolean transformed) {
        put(TRANSFORMED, transformed);
    }

    public boolean isExtended() {
        return (boolean) get(EXTENDED);
    }

    public void setExtended(boolean extended) {
        put(EXTENDED, extended);
    }
}
