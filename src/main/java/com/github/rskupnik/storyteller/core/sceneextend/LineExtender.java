package com.github.rskupnik.storyteller.core.sceneextend;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import org.javatuples.Pair;

import java.util.List;

public class LineExtender implements SceneExtender {

    @Override
    public void extend(TransformedScene scene) {
        int line = 1;
        float lastX = 0;
        for (Pair<Actor, List<Fragment>> actorToDataPair : scene.getData()) {
            Actor actor = actorToDataPair.getValue0();

            for (Fragment fragment : actorToDataPair.getValue1()) {
                GlyphLayout GL = (GlyphLayout) fragment.get("glyphLayout");
                Vector2 pos = (Vector2) fragment.get("position");

                if (pos.x <= lastX) {
                    line++;
                }
                lastX = pos.x;

                if (!actor.getInternalActor().isExtended())
                    fragment.put("line", line);
            }
        }
    }
}
