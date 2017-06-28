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

    private boolean inProgress = false;     // Determines if any line is being isAppearing right now
    //private boolean finished = false;       // Determines if the whole text is visible and done tweening
    private int currentlyProcessedLine = 1;
    private int disappearLineThreshold = -1;
    private long timestampAppear = 0;             // Needed to line the effects one after another
    private long timestampDisappear = 0;
    private Map<Integer, Float> linePosition = new HashMap<>();
    private float yAdjust = 0;
    private boolean isAppearing = false;
    private boolean isDisappearing = false;
    private int highestLine = 0;
    private boolean suspended = false;
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

        if (scenePair.scene().isDirty()) {
            System.out.println("Scene is dirty");
            if (suspended) {
                System.out.println("UNSUSPENDED");
                suspended = false;
            }
            isAppearing = false;
            currentlyProcessedLine--;
            System.out.println("CurrentLine: "+currentlyProcessedLine);
        }

        boolean isAppearingInternal = false;
        boolean isDisappearingInternal = false;
        boolean firstDisappearProcessed = false;
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

                if (linePosition.get(line) == null) {
                    linePosition.put(line, position.y);
                }

                if (line > highestLine) {
                    highestLine = line;
                    //suspended = false;
                    System.out.println("highestLine="+highestLine);
                }

                if (!isAppearing && !suspended) {
                    if (line == currentlyProcessedLine && !processed) {
                        System.out.println("Tweening line "+line);
                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                .target(1.0f)
                                .ease(equation)
                                .start(tweenManager);

                        isAppearingInternal = true;
                        stateFlags.put("processed", true);
                    }
                }

                if (disappearEnabled && !isDisappearing) {
                    if (line == disappearLineThreshold) {
                        System.out.println("Disappearing line: " + line);
                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                .target(0f)
                                .ease(equation)
                                .start(tweenManager);
                        isDisappearingInternal = true;
                    }
                }

                if ((line <= currentlyProcessedLine || processed) && line >= disappearLineThreshold)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset() + offset.y);
            }
        }

        if (isAppearingInternal)
            isAppearing = true;

        if (isDisappearingInternal && disappearEnabled)
            isDisappearing = true;

        boolean shouldMove = false;
        if (disappearEnabled && System.currentTimeMillis() > timestampDisappear) {

            if (disappearLineThreshold != highestLine)
                disappearLineThreshold++;

            isDisappearing = false;

            timestampDisappear = System.currentTimeMillis() + disappearInterval;
            shouldMove = true;
        }

        if (System.currentTimeMillis() > timestampAppear) {

            if (currentlyProcessedLine != highestLine+1)
                currentlyProcessedLine++;
            else
                suspended = true;

            isAppearing = false;

            timestampAppear = System.currentTimeMillis() + appearInterval;

            shouldMove = true;

            System.out.println("TICK, currentLine: "+currentlyProcessedLine+"; disappearLine: "+disappearLineThreshold);
        }

        // TODO: BUG - text goes up beyond upper threshold
        if (shouldMove) {
            Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                    .target(offset.y + 5)
                    .ease(equation)
                    .start(tweenManager);
        }
    }

    public void render3(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, ScenePair scenePair) {
        if (font == null || batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        if (scenePair.scene().isDirty()) {
            System.out.println("Scene is dirty");
            if (suspended) {
                System.out.println("UNSUSPENDED");
                suspended = false;
            }
            isAppearing = false;
            currentlyProcessedLine--;
            System.out.println("CurrentLine: "+currentlyProcessedLine);
        }

        boolean isAppearingInternal = false;
        boolean isDisappearingInternal = false;
        boolean firstDisappearProcessed = false;
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

                if (linePosition.get(line) == null) {
                    linePosition.put(line, position.y);
                }

                if (line > highestLine) {
                    highestLine = line;
                    //suspended = false;
                    System.out.println("highestLine="+highestLine);
                }

                if (!isAppearing && !suspended) {
                    if (line == currentlyProcessedLine && !processed) {
                        System.out.println("Tweening line "+line);
                        Timeline.createSequence()
                                .beginParallel()
                                .push(
                                        Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                                                .target(offset.y + 5)
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

                if (disappearEnabled && !isDisappearing) {
                    if (line == disappearLineThreshold) {
                        System.out.println("Disappearing line: " + line);
                        Timeline.createSequence()
                                .beginParallel()
                                .push(
                                        Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                                                .target(offset.y + 5)
                                                .ease(equation)
                                ).push(
                                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                                .target(0f)
                                                .ease(equation)
                                )
                                .end()
                                .start(tweenManager);

                    } else if (line > disappearLineThreshold) {
                        System.out.println("Moving line up: "+line);
                        Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                                .target(offset.y + GL.runs.get(0).glyphs.get(0).height)
                                .ease(equation)
                                .start(tweenManager);
                    }
                    isDisappearingInternal = true;
                }

                if ((line <= currentlyProcessedLine || processed) && line >= disappearLineThreshold)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset() + offset.y);
            }
        }

        if (isAppearingInternal)
            isAppearing = true;

        if (isDisappearingInternal && disappearEnabled)
            isDisappearing = true;

        if (System.currentTimeMillis() > timestampAppear) {

            if (currentlyProcessedLine != highestLine+1)
                currentlyProcessedLine++;
            else
                suspended = true;

            isAppearing = false;

            if (disappearEnabled) {
                if (disappearLineThreshold != highestLine)
                    disappearLineThreshold++;

                isDisappearing = false;
            }

            timestampAppear = System.currentTimeMillis() + duration;

            System.out.println("TICK, currentLine: "+currentlyProcessedLine+"; disappearLine: "+disappearLineThreshold);
        }
    }

    public void render2(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, ScenePair scenePair) {
        if (font == null || batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        if (inProgress && System.currentTimeMillis() > timestampAppear) {
            inProgress = false;
            currentlyProcessedLine++;
        }

        boolean atLeastOneProcessed = false;
        for (Pair<Actor, List<Fragment>> actorToDataPair : data.getData()) {
            Actor actor = actorToDataPair.getValue0();
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) actorData.get("glyphLayout");
                Rectangle rectangle = (Rectangle) actorData.get("clickableArea");
                Vector2 position = (Vector2) actorData.get("position");
                Integer line = (Integer) actorData.get("line");
                Color color = (Color) actorData.get("color");

                if (GL == null || position == null)
                    continue;

                if (linePosition.get(line) == null) {
                    linePosition.put(line, position.y);
                }

                if (!inProgress && line == currentlyProcessedLine) {    // Should start tweening this line
                    atLeastOneProcessed = true;

                    Timeline sequence = Timeline.createSequence();
                    sequence.beginParallel()
                            .push(
                                Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                    .target(position.y + 5)
                                    .ease(equation)
                            ).push(
                                Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                    .target(1.0f)
                                    .ease(equation)
                            )
                            .end();
                    if (appearInterval > 0) {
                        int i = line;
                        while (i > 1) {
                            sequence.push(
                                    Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                            .target(linePosition.get(i - 1))
                                            .ease(equation)
                                    //.delay(lingerTime / 1000f)
                            );
                            i--;
                        }
                        sequence.beginParallel()
                                .push(
                                        Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                                .target(linePosition.get(1) + 5.0f)
                                                .ease(equation)
                                                .delay(appearInterval / 1000f)
                                )
                                .push(
                                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                                .target(0f)
                                                .ease(equation)
                                                .delay(appearInterval / 1000f)
                                )
                                .end();
                    }
                    sequence.start(tweenManager);
                }

                if (line <= currentlyProcessedLine)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
            }
        }

        if (atLeastOneProcessed) {
            timestampAppear = System.currentTimeMillis() + duration;
            inProgress = true;
        }
    }
}
