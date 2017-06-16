package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.peripheral.Scene;
import com.github.rskupnik.storyteller.peripheral.Stage;

import java.util.HashMap;
import java.util.Map;

public final class Linkers {

    public Map<Scene, Stage> sceneToStage = new HashMap<>();
    public Map<Stage, Scene> stageToScene = new HashMap<>();
}
