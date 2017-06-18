package com.github.rskupnik.storyteller.peripheral.internals;

public final class InternalActor {

    private float yOffset;
    private boolean processed;  // Used in rendering and several AppearEffects to indicate processing status

    public float getYOffset() {
        return yOffset;
    }

    public void setYOffset(float yOffset) {
        this.yOffset = yOffset;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
