package com.github.rskupnik.storyteller.core.sceneextend;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import org.javatuples.Pair;

import java.util.List;

public class LineExtender implements SceneExtender {

    @Override
    public void extend(TransformedScene scene) {
        int line = 1;
        float lastX = 0;
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : scene.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();

            for (Fragment fragment : actorToDataPair.getValue1()) {
                GlyphLayout GL = (GlyphLayout) fragment.get(FragmentId.GLYPH_LAYOUT);
                Vector2 pos = (Vector2) fragment.get(FragmentId.POSITION);

                if (pos.x <= lastX) {
                    line++;
                }
                lastX = pos.x;

                if (!actor.state().isExtended())
                    fragment.put(FragmentId.LINE, line);
            }
        }
    }
}
