package com.github.rskupnik.storyteller.core;

import aurelienribon.tweenengine.TweenManager;
import com.github.rskupnik.storyteller.aggregates.*;
import com.github.rskupnik.storyteller.core.renderingunits.background.factory.IBackgroundRenderingUnitFactory;
import com.github.rskupnik.storyteller.core.renderingunits.text.factory.IRenderingUnitFactory;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.statefulobjects.objects.Scene;
import com.github.rskupnik.storyteller.statefulobjects.states.SceneState;
import com.github.rskupnik.storyteller.utils.SceneUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SceneHandler {

    @Inject Scenes scenes;
    @Inject Stages stages;
    @Inject Clickables clickables;
    @Inject SceneUtils sceneUtils;
    @Inject SceneSwaps sceneSwaps;

    @Inject
    public SceneHandler() {

    }

    public void attachScene(String stageId, Scene scene) {
        StatefulScene statefulScene = new StatefulScene(scene, new SceneState());
        scenes.add(statefulScene);

        StatefulStage statefulStage = stages.find(stageId);
        if (statefulStage == null)
            throw new IllegalStateException("Cannot attach to stage "+stageId+" as it doesn't exist");

        if (statefulStage.state().getAttachedScene() != null) {
            prepareForSwap(statefulStage, statefulScene);
        } else
            activateScene(statefulStage, statefulScene);
    }

    private void prepareForSwap(StatefulStage stage, StatefulScene scene) {
        sceneSwaps.put(stage, scene);
        stage.state().getAttachedScene().state().setExitSequenceStarted(true);
    }

    /**
     * Mutually attaches the stage and scene together
     * and transforms the scene.
     */
    public StatefulScene activateScene(StatefulStage stage, StatefulScene scene) {
        stage.state().attachScene(scene);
        scene.state().attachStage(stage);

        sceneUtils.transform(scene);

        return scene;
    }

    public void removeScene(Scene scene) {
        removeScene(scene.getId());
    }

    public void removeScene(String id) {
        StatefulScene statefulScene = scenes.find(id);

        if (statefulScene != null) {
            clickables.removeScene(statefulScene);
            scenes.remove(statefulScene);

            StatefulStage stagePair = statefulScene.state().getAttachedStage();
            stagePair.state().attachScene(null);
            statefulScene.state().attachStage(null);
        }
    }
}
