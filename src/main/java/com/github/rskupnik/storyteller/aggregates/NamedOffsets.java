package com.github.rskupnik.storyteller.aggregates;

import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.structs.Vault;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;

/**
 * Contains various named offsets to be used across the system.
 *
 * TODO: Is there a way to make this use Vault instead? The problem is we can't attach id to an enum - or can we, as a param?
 */
@Singleton
public class NamedOffsets extends HashMap<String, Vector2> {

    @Inject
    public NamedOffsets() {

    }
}
