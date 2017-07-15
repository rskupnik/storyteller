package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.javatuples.Pair;

import java.util.List;

@Singleton
public class SceneUtils {

    @Inject SceneTransformer sceneTransformer;

    @Inject
    public SceneUtils() {

    }

    public void transform(StatefulScene statefulScene) {
        StatefulStage statefulStage = statefulScene.state().getAttachedStage();
        if (statefulStage == null)
            return;

        TransformedScene existingScene = statefulScene.state().getTransformedScene();

        TransformedScene transformedScene = existingScene == null ?
                sceneTransformer.transform(statefulScene) :
                sceneTransformer.transform(existingScene, statefulScene);

        if (statefulStage.state().getRenderingUnit() != null)
            statefulStage.state().getRenderingUnit().getChain().apply(transformedScene);
        //scenePair.scene().setDirty(false);
        statefulScene.state().setTransformedScene(transformedScene);
    }

    // TODO: This is bad, it's better to use font.fontData.lineHeight + font.fontData.blankLineScale
    public int extractLargestLineHeight(TransformedScene scene) {
        int highest = 0;
        for (Pair<StatefulActor, List<Fragment>> pair : scene.getData()) {
            for (Fragment fragment : pair.getValue1()) {
                GlyphLayout gl = (GlyphLayout) fragment.get("glyphLayout");
                if (gl != null && gl.runs != null) {
                    for (GlyphLayout.GlyphRun gr : gl.runs) {
                        if (gr != null) {
                            for (BitmapFont.Glyph g : gr.glyphs) {
                                if (g != null && g.height > 3 && g.height > highest)
                                    highest = g.height;
                            }
                        }
                    }
                }
            }
        }
        return highest;
    }
}
