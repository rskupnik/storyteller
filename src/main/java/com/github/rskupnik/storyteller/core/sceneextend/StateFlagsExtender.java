package com.github.rskupnik.storyteller.core.sceneextend;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rskupnik.storyteller.core.scenetransform.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.peripheral.Actor;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;

public class StateFlagsExtender implements SceneExtender {

    @Override
    public void extend(TransformedScene scene) {
        for (Pair<Actor, List<Fragment>> actorToDataPair : scene.getData()) {
            Actor actor = actorToDataPair.getValue0();
            if (actor.getInternalActor().isExtended())
                continue;

            for (Fragment fragment : actorToDataPair.getValue1()) {
                fragment.put("stateFlags", new HashMap<String, Boolean>());
            }
        }
    }
}
