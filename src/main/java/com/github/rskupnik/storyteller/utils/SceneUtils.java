package com.github.rskupnik.storyteller.utils;

import com.github.rskupnik.storyteller.core.scenetransform.SceneTransformer;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.github.rskupnik.storyteller.wrappers.pairs.StagePair;
import com.google.inject.Inject;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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

    // TODO: Implement this
    public int extractLineHeight(Scene scene) {
        throw new NotImplementedException();
    }
}
