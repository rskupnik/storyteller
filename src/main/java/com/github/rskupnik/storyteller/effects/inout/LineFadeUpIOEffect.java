package com.github.rskupnik.storyteller.effects.inout;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.core.sceneextend.*;
import com.github.rskupnik.storyteller.core.scenetransform.Fragment;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
// TODO: Rename this line of classes to RenderingUnit(s) and make them internal - they should be created via initializer stub with settings only
public final class LineFadeUpIOEffect extends IOEffect {

    private final TweenEquation equation;
    private final int duration;
    private final int appearInterval;
    private final int disappearInterval;
    private final boolean disappearEnabled;

    // TODO: Comment these and the algorithm
    private int currentlyProcessedLine_Appear = 1;
    private int currentlyProcessedLine_Disappear = -1;
    private long timestampAppear = 0;
    private long timestampDisappear = 0;
    private boolean isAppearing = false;
    private boolean isDisappearing = false;
    private int highestLine = 0;
    private boolean appearingSuspended = false;
    private boolean disappearingSuspended = false;
    private Vector2 offset = new Vector2(0, 0);

    public LineFadeUpIOEffect(TweenEquation equation, int duration, int appearInterval, int disappearInterval) {
        super(ExtenderChain.from(new LineExtender(), new ColorToTransparentExtender(), new PullDownExtender(), new StateFlagsExtender()));
        this.equation = equation;
        this.duration = duration;
        this.appearInterval = appearInterval;
        this.disappearInterval = disappearInterval;
        this.disappearEnabled = disappearInterval > 0;
    }

    @Override
    public void render(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, ScenePair scenePair) {
        if (font == null || batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        if (scenePair.scene().isDirty()) {  // When the scene is dirty, need to un-suspend the algorithm.
            System.out.println("Scene is dirty");
            if (appearingSuspended) {
                System.out.println("UNSUSPENDED");
                appearingSuspended = false;
            }
            isAppearing = false;
            currentlyProcessedLine_Appear--;    // Why is this decreased?
            System.out.println("CurrentLine: "+ currentlyProcessedLine_Appear);

            if (disappearingSuspended) {
                disappearingSuspended = false;
            }
            isDisappearing = false;
            //currentlyProcessedLine_Disappear--;
        }

        boolean isAppearingInternal = false;
        boolean isDisappearingInternal = false;
        for (Pair<Actor, List<Fragment>> actorToDataPair : data.getData()) {
            Actor actor = actorToDataPair.getValue0();
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) actorData.get("glyphLayout");
                Rectangle rectangle = (Rectangle) actorData.get("clickableArea");
                Vector2 position = (Vector2) actorData.get("position");
                Integer line = (Integer) actorData.get("line");
                Color color = (Color) actorData.get("color");
                Map<String, Boolean> stateFlags = (Map<String, Boolean>) actorData.get("stateFlags");
                boolean processed = stateFlags.get("processed") != null ? stateFlags.get("processed") : false;

                if (GL == null || position == null)
                    continue;

                if (line > highestLine) {
                    highestLine = line;
                    //appearingSuspended = false;
                    System.out.println("highestLine="+highestLine);
                }

                if (!isAppearing && !appearingSuspended) {
                    if (line == currentlyProcessedLine_Appear && !processed) {
                        System.out.println("Tweening line "+line);
                        Timeline.createSequence()
                                .beginParallel()
                                .push(
                                        Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                                .target(position.y + 5)
                                                .ease(equation)
                                ).push(
                                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                                .target(1.0f)
                                                .ease(equation)
                                )
                                .end()
                                .start(tweenManager);

                        isAppearingInternal = true;
                        stateFlags.put("processed", true);
                    }
                }

                if (disappearEnabled && !isDisappearing && !disappearingSuspended) {
                    if (line == currentlyProcessedLine_Disappear) {
                        System.out.println("Disappearing line: " + line);
                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                .target(0f)
                                .ease(equation)
                                .start(tweenManager);
                        isDisappearingInternal = true;
                    }
                }

                if ((line <= currentlyProcessedLine_Appear || processed) && line >= currentlyProcessedLine_Disappear)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset() + offset.y);
            }
        }

        if (isAppearingInternal)
            isAppearing = true;

        if (isDisappearingInternal && disappearEnabled)
            isDisappearing = true;

        if (disappearEnabled && System.currentTimeMillis() > timestampDisappear) {

            if (currentlyProcessedLine_Disappear != highestLine) {
                currentlyProcessedLine_Disappear++;

                if (currentlyProcessedLine_Disappear > 0) {
                    Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                            .target(offset.y + 18)
                            .ease(equation)
                            .start(tweenManager);
                }
            } else
                disappearingSuspended = true;

            isDisappearing = false;

            timestampDisappear = System.currentTimeMillis() + disappearInterval;
        }

        if (System.currentTimeMillis() > timestampAppear) {

            if (currentlyProcessedLine_Appear != highestLine+1)
                currentlyProcessedLine_Appear++;
            else
                appearingSuspended = true;

            isAppearing = false;

            timestampAppear = System.currentTimeMillis() + appearInterval;

            System.out.println("TICK, currentLine: "+ currentlyProcessedLine_Appear +"; disappearLine: "+ currentlyProcessedLine_Disappear);
        }
    }
}
