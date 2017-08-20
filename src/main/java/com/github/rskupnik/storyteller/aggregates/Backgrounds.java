package com.github.rskupnik.storyteller.aggregates;

import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Singleton
public class Backgrounds {

    private Map<StatefulStage, Background> current = new HashMap<>();
    private Map<StatefulStage, Background> awaiting = new HashMap<>();

    @Inject
    public Backgrounds() {

    }

    public void setCurrent(StatefulStage stage, Background current) {
        this.current.put(stage, current);
    }

    public void setAwaiting(StatefulStage stage, Background awaiting) {
        this.awaiting.put(stage, awaiting);
    }

    public Background current(StatefulStage stage) {
        return current.get(stage);
    }

    public Background awaiting(StatefulStage stage) {
        return awaiting.get(stage);
    }
}
