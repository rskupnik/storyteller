package com.github.rskupnik.storyteller.statefulobjects.states;

import com.github.rskupnik.storyteller.structs.State;

public class ActorState extends State {

    private static final String Y_OFFSET = "yOffset";
    private static final String TRANSFORMED = "transformed";
    private static final String EXTENDED = "extended";
    private static final String PROCESSED = "processed";

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
