package com.github.rskupnik.storyteller.core.rendering;

import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.rendering.background.BasicBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.core.rendering.background.NormalMappedBackgroundRenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.backgrounds.BasicBackground;
import com.github.rskupnik.storyteller.structs.backgrounds.NormalMappedBackground;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public final class RenderingUnitPicker {

    @Inject Commons commons;

    private Map<StatefulScene, RenderingUnit> backgroundRenderingUnits = new HashMap<>();
    private Map<StatefulScene, RenderingUnit> textRenderingUnits = new HashMap<>();

    @Inject
    public RenderingUnitPicker() {

    }

    public RenderingUnit pick(Background background) {
        //RenderingUnit current = backgroundRenderingUnits.get(scene);
        if (background instanceof BasicBackground) {
            /*if (current == null || !(current instanceof BasicBackgroundRenderingUnit)) {
                current = commons.injector.basicBgRU2();
                backgroundRenderingUnits.put(scene, current);
            }*/
            return commons.injector.basicBgRU2();
        } else if (background instanceof NormalMappedBackground) {
            /*if (current == null || !(current instanceof NormalMappedBackgroundRenderingUnit)) {
                current = commons.injector.normalMappedBgRU2();
                backgroundRenderingUnits.put(scene, current);
            }*/
            return commons.injector.normalMappedBgRU2();
        } else return null;
        //return current;
    }
}
