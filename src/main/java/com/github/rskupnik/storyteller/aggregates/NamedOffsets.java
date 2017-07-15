package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.structs.DataBank;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

/**
 * Contains various named offsets to be used across the system.
 */
@Singleton
public class NamedOffsets extends DataBank<Vector2> {

    @Inject
    public NamedOffsets() {

    }
}
