package com.github.rskupnik.storyteller.core.renderingunits.text;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.NamedOffsets;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.*;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import javax.inject.Inject;
import org.javatuples.Pair;

import java.util.List;
import java.util.Map;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeFloatRenderingUnit extends RenderingUnit {

    @Inject Commons commons;
    @Inject TweenManager tweenManager;
    @Inject Clickables clickables;
    @Inject NamedOffsets namedOffsets;
    private SceneUtils sceneUtils = new SceneUtils();

    private TweenEquation equation;
    private int duration;
    private int appearInterval;
    private int disappearInterval;
    private boolean disappearEnabled;

    private int currentlyProcessedLine_Appear = 1;              // Denotes the line currently being processed by the appear part
    private int currentlyProcessedLine_Disappear = -1;          // Same but for the disappear part
    private long timestampAppear = 0;                           // Used to fire the algorithm in equal period of time
    private long timestampDisappear = 0;                        // Same but for the disappear part
    private boolean isAppearing = false;                        // Set to true if a text is being tweened right now to avoid processing the other ones
    private boolean isDisappearing = false;
    private int highestLine = 0;                                // Stored to determine when the algorithm has processed all the lines
    private boolean appearingSuspended = false;                 // Denotes whether appearing part is suspended
    private boolean disappearingSuspended = false;              // Same but for disappearing
    private Vector2 offset = new Vector2(0, 0);           // Holds the offset that all actors will move (only Y is used)
    private int lineHeight = 0;

    @Inject
    public LineFadeFloatRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {
        setChain(ExtenderChain.from(new LineExtender(), new ColorToTransparentExtender(), new PullDownExtender(), new StateFlagsExtender()));

        LineFadeFloatInitializer initializerLFF = (LineFadeFloatInitializer) initializer;
        this.equation = initializerLFF.getEquation();
        this.duration = initializerLFF.getDuration();
        this.appearInterval = initializerLFF.getAppearInterval();
        this.disappearInterval = initializerLFF.getDisappearInterval();
        this.disappearEnabled = disappearInterval > 0;
    }

    @Override
    public void render(float delta, StatefulScene scenePair) {
        if (commons.font == null || commons.batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.state().getTransformedScene();
        if (data == null)
            return;

        if (lineHeight == 0) {
            lineHeight = sceneUtils.extractLargestLineHeight(data);
            System.out.println("LINE HEIGHT: "+lineHeight);
        }

        //region Handle Dirty Scene
        if (scenePair.obj().isDirty()) {  // When the scene is dirty, need to un-suspend the algorithm.
            if (appearingSuspended)
                appearingSuspended = false;

            isAppearing = false;
            currentlyProcessedLine_Appear--;    // Why is this decreased?

            if (disappearingSuspended)
                disappearingSuspended = false;

            isDisappearing = false;
            //currentlyProcessedLine_Disappear--;
        }
        //endregion

        if (namedOffsets.get("LFF-offset-"+scenePair.obj().getId()) == null)
            namedOffsets.put("LFF-offset-"+scenePair.obj().getId(), offset);

        boolean isAppearingInternal = false;    // These are set if at least one fragment is processed, based on those the larger flags are set later
        boolean isDisappearingInternal = false;
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) actorData.get("glyphLayout");
                Rectangle rectangle = (Rectangle) actorData.get("clickableArea");
                Vector2 position = (Vector2) actorData.get("position");
                Integer line = (Integer) actorData.get("line");
                Color color = (Color) actorData.get("color");
                Map<String, Boolean> stateFlags = (Map<String, Boolean>) actorData.get("stateFlags");
                boolean processed = stateFlags.get("processed") != null ? stateFlags.get("processed") : false;

                if (GL == null || position == null || line == null || stateFlags == null)
                    continue;

                if (line > highestLine) {
                    highestLine = line;
                }

                //region Process Appearing Part
                if (!isAppearing && !appearingSuspended) {
                    if (line == currentlyProcessedLine_Appear && !processed) {
                        // Float-up and fade-in
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
                //endregion

                //region Process Disappearing Part
                if (disappearEnabled && !isDisappearing && !disappearingSuspended) {
                    if (line == currentlyProcessedLine_Disappear) {
                        // Fade-out. The float-up part is handled by adjusting the offset variable
                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                .target(0f)
                                .ease(equation)
                                .start(tweenManager);
                        isDisappearingInternal = true;

                        if (rectangle != null)
                            clickables.removeRectangle(scenePair, rectangle);
                    }
                }
                //endregion

                if ((line <= currentlyProcessedLine_Appear || processed) && line >= currentlyProcessedLine_Disappear)
                    commons.font.draw(commons.batch, GL, position.x, position.y + actor.state().getYOffset() + offset.y);
            }
        }

        if (isAppearingInternal)
            isAppearing = true;

        if (isDisappearingInternal && disappearEnabled)
            isDisappearing = true;

        //region Manage Disappear Part
        if (disappearEnabled && System.currentTimeMillis() > timestampDisappear) {

            if (currentlyProcessedLine_Disappear != highestLine) {
                currentlyProcessedLine_Disappear++;

                if (currentlyProcessedLine_Disappear > 0) { // Increase the offset to make the whole text move and new text to be added with the offset included
                    Tween.to(offset, Vector2Accessor.Y, duration / 1000f)
                            .target(offset.y + commons.font.getData().lineHeight + commons.font.getData().blankLineScale)
                            .ease(equation)
                            .start(tweenManager);
                }
            } else
                disappearingSuspended = true;

            isDisappearing = false;
            timestampDisappear = System.currentTimeMillis() + disappearInterval;
        }
        //endregion

        //region Manage Appear Part
        if (System.currentTimeMillis() > timestampAppear) {

            if (currentlyProcessedLine_Appear != highestLine+1)
                currentlyProcessedLine_Appear++;
            else
                appearingSuspended = true;

            isAppearing = false;
            timestampAppear = System.currentTimeMillis() + appearInterval;
        }
        //endregion
    }
}