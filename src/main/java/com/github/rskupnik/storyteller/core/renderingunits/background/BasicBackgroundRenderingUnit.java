package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import javax.inject.Inject;

public final class BasicBackgroundRenderingUnit extends BackgroundRenderingUnit {

    @Inject
    Commons commons;

    @Inject
    public BasicBackgroundRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {

    }

    @Override
    public void render(float delta, StatefulStage statefulStage) {
        if (statefulStage.obj().getBackgroundImage() != null) {
            Texture backgroundImage = statefulStage.obj().getBackgroundImage();
            Rectangle rect = statefulStage.obj().getRectangle();
            commons.batch.draw(backgroundImage, rect.x, rect.y, rect.getWidth(), rect.getHeight());
        }
    }
}
