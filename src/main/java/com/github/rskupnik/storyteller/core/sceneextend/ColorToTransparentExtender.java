package com.github.rskupnik.storyteller.core.sceneextend;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import org.javatuples.Pair;

import java.util.List;

public class ColorToTransparentExtender implements SceneExtender{

    @Override
    public void extend(TransformedScene scene) {
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : scene.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            if (actor.state().isExtended())
                continue;

            for (Fragment fragment : actorToDataPair.getValue1()) {
                GlyphLayout GL = (GlyphLayout) fragment.get("glyphLayout");
                Color color = (Color) fragment.get("color");
                if (color == null)
                    color = GL.runs.get(0).color;
                color.a = 0;
                fragment.put("color", color);
            }
        }
    }
}
