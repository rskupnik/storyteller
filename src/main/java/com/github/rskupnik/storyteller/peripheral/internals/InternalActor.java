package com.github.rskupnik.storyteller.peripheral.internals;

public final class InternalActor {

    private float yOffset;
    private boolean transformed;
    private boolean extended;
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

    public boolean isTransformed() {
        return transformed;
    }

    public void setTransformed(boolean transformed) {
        this.transformed = transformed;
    }

    public boolean isExtended() {
        return extended;
    }

    public void setExtended(boolean extended) {
        this.extended = extended;
    }
}
