package com.github.rskupnik.storyteller.core.rendering.background;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.aggregates.Backgrounds;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.rendering.RenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.backgrounds.BasicBackground;

import javax.inject.Inject;

public class BasicBackgroundRenderingUnit extends RenderingUnit {

    @Inject Commons commons;
    @Inject TweenManager tweenManager;
    @Inject Backgrounds backgrounds;

    private boolean exitSequenceInitiated = false;
    private long exitTimestamp = 0;

    @Inject
    public BasicBackgroundRenderingUnit() {

    }

    @Override
    public void render(float delta, StatefulStage stage) {
        super.render(delta, stage);

        if (System.currentTimeMillis() - exitTimestamp > 1000) {
            exitSequenceInitiated = false;
            exitTimestamp = 0;
        }

        final Background background = backgrounds.current(stage);
        if (background == null)
            return;

        if (!exitSequenceInitiated && background.isExitSequenceStarted()) {
            if (background.getTint() != null) {
                Tween.to(background.getTint(), ColorAccessor.ALPHA, 1)
                        .target(0f)
                        .setCallback(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                background.setExitSequenceFinished(true);
                            }
                        })
                        .start(tweenManager);
            }
            exitSequenceInitiated = true;
            exitTimestamp = System.currentTimeMillis();
        }

        commons.batch.begin();

        Color c = commons.batch.getColor();
        if (background.getTint() != null)
            commons.batch.setColor(background.getTint());

        Texture backgroundImage = background.getImage();
        Rectangle rect = stage.obj().getBackgroundArea();
        if (rect == null)
            rect = stage.obj().getRectangle();

        commons.batch.draw(backgroundImage, rect.x, rect.y, rect.getWidth(), rect.getHeight());
        commons.batch.setColor(c);

        commons.batch.end();
    }

    @Override
    public void preFirstRender(StatefulStage stage) {
        Background background = backgrounds.current(stage);
        if (background != null && background instanceof BasicBackground) {
            Color c = ((BasicBackground) background).getPostPublishColor();
            if (background.getTint() != null && c != null) {
                Tween.to(background.getTint(), ColorAccessor.ALPHA, 1)
                        .target(c.a)
                        .start(tweenManager);
            }
        }
    }
}
