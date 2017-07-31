package com.github.rskupnik.storyteller.core.sceneextend;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import org.javatuples.Pair;

import java.util.List;

public class ColorExtender implements SceneExtender {

    @Override
    public void extend(TransformedScene scene) {
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : scene.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            if (actor.state().isExtended())
                continue;

            for (Fragment fragment : actorToDataPair.getValue1()) {
                GlyphLayout GL = (GlyphLayout) fragment.get(FragmentId.GLYPH_LAYOUT);
                fragment.put(FragmentId.COLOR, GL.runs.get(0).color);
            }
        }
    }
}
