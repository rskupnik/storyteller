package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.core.lighting.Light;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class Lights extends ArrayList<Light> {

    @Inject
    public Lights() {

    }
}
