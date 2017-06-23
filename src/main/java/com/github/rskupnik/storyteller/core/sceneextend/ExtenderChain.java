package com.github.rskupnik.storyteller.core.sceneextend;

import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;

import java.util.ArrayList;

public class ExtenderChain extends ArrayList<SceneExtender> {

    private ExtenderChain(int size) {
        super(size);
    }

    public static ExtenderChain from(SceneExtender... extenders) {
        ExtenderChain list = new ExtenderChain(extenders.length);
        for (SceneExtender extender : extenders) {
            list.add(extender);
        }
        return list;
    }

    public void apply(TransformedScene scene) {
        for (SceneExtender extender : this) {
            extender.extend(scene);
        }
    }
}
