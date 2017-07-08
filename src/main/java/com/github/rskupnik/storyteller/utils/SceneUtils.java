package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import org.javatuples.Pair;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class SceneUtils {

    @Inject private SceneTransformer sceneTransformer;

    public void transform(ScenePair scenePair) {
        StagePair stagePair = scenePair.internal().getAttachedStage();
        if (stagePair == null)
            return;

        TransformedScene existingScene = scenePair.internal().getTransformedScene();

        TransformedScene transformedScene = existingScene == null ?
                sceneTransformer.transform(scenePair) :
                sceneTransformer.transform(existingScene, scenePair);

        if (stagePair.internal().getRenderingUnit() != null)
            stagePair.internal().getRenderingUnit().getChain().apply(transformedScene);
        //scenePair.scene().setDirty(false);
        scenePair.internal().setTransformedScene(transformedScene);
    }

    // TODO: This is bad, it's better to use font.fontData.lineHeight + font.fontData.blankLineScale
    public int extractLargestLineHeight(TransformedScene scene) {
        int highest = 0;
        for (Pair<Actor, List<Fragment>> pair : scene.getData()) {
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
