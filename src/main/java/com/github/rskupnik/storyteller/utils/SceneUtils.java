package com.github.rskupnik.storyteller.utils;

import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;

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

        if (stagePair.stage().getIOEffect() != null)
            stagePair.stage().getIOEffect().getChain().apply(transformedScene);
        scenePair.scene().setDirty(false);
        scenePair.internal().setTransformedScene(transformedScene);
    }
}
