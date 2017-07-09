package com.github.rskupnik.storyteller.core.sceneextend;

import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;

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

        for (Pair<StatefulActor, List<Fragment>> pair : scene.getData()) {
            pair.getValue0().state().setExtended(true);
        }
    }
}
