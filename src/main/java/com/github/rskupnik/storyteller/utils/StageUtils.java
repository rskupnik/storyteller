package com.github.rskupnik.storyteller.utils;

import com.github.rskupnik.storyteller.peripheral.Stage;

public final class StageUtils {

    /**
     * Calculates the remaining width available to fill with text in the current text row.
     */
    public static int calcRemainingWidth(Stage stage, int currentX) {
        return stage.getWidth() - (currentX - (int) stage.getTopLeft().x);
    }

    /**
     * Checks whether x points to the beginning of a new text line or somewhere in an existing one.
     */
    public static boolean notStartOfLine(Stage stage, int x) {
        return x != (int) stage.getTopLeft().x;
    }
}
