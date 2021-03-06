package com.github.rskupnik.storyteller.core.sceneextend;

import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;

public class StateFlagsExtender implements SceneExtender {

    @Override
    public void extend(TransformedScene scene) {
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : scene.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            if (actor.state().isExtended())
                continue;

            for (Fragment fragment : actorToDataPair.getValue1()) {
                fragment.put(FragmentId.STATE_FLAGS, new HashMap<String, Boolean>());
            }
        }
    }
}
