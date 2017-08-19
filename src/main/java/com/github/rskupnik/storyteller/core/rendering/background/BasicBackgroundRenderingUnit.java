package com.github.rskupnik.storyteller.core.rendering.background;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.rendering.RenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;

import javax.inject.Inject;

public class BasicBackgroundRenderingUnit extends RenderingUnit {

    @Inject Commons commons;

    @Inject
    public BasicBackgroundRenderingUnit() {

    }

    @Override
    public void render(float delta, StatefulScene scene) {
        super.render(delta, scene);

        Background background = scene.obj().getBackground();
        StatefulStage stage = scene.state().getAttachedStage();
        if (stage == null || background == null)
            return;

        commons.batch.begin();
        Texture backgroundImage = background.getImage();
        Rectangle rect = scene.obj().getBackgroundArea();
        if (rect == null)
            rect = stage.obj().getRectangle();
        commons.batch.draw(backgroundImage, rect.x, rect.y, rect.getWidth(), rect.getHeight());
        commons.batch.end();
    }

    @Override
    public void preFirstRender(StatefulScene scene) {
        // Intentionally empty
    }
}
