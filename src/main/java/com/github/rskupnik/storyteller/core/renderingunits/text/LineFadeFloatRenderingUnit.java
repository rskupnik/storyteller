package com.github.rskupnik.storyteller.core.renderingunits.text;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.aggregates.Clickables;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Lights;
import com.github.rskupnik.storyteller.aggregates.NamedOffsets;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.LineFadeFloatInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.*;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import com.github.rskupnik.storyteller.utils.FileUtils;
import com.github.rskupnik.storyteller.utils.LightUtils;
import com.github.rskupnik.storyteller.utils.SceneUtils;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import javax.inject.Inject;

import com.github.rskupnik.storyteller.utils.ShaderUtils;
import org.javatuples.Pair;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeFloatRenderingUnit extends RenderingUnit {

    @Inject Commons commons;
    @Inject Lights lights;
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
    private boolean exitInProgress = false;

    private boolean affectedByLight;
    private ShaderProgram shader;
    private Light light;

    private int disappearInterval_persisted;
    private boolean disappearEnabled_persisted;

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
        this.disappearInterval_persisted = disappearInterval;
        this.disappearEnabled = disappearInterval > 0;
        this.disappearEnabled_persisted = disappearEnabled;
        this.affectedByLight = initializerLFF.isAffectedByLight();
    }

    @Override
    public void reset() {
        currentlyProcessedLine_Appear = 1;
        currentlyProcessedLine_Disappear = -1;
        timestampAppear = 0;
        timestampDisappear = 0;
        isAppearing = false;
        isDisappearing = false;
        highestLine = 0;
        appearingSuspended = false;
        disappearingSuspended = false;
        offset = new Vector2(0, 0);
        lineHeight = 0;
        exitInProgress = false;

        disappearInterval = disappearInterval_persisted;
        disappearEnabled = disappearEnabled_persisted;
    }

    @Override
    public void render(float delta, StatefulScene scenePair) {
        super.render(delta, scenePair);

        if (commons.font == null || commons.batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.state().getTransformedScene();
        if (data == null)
            return;

        if (lineHeight == 0) {
            lineHeight = SceneUtils.extractLineHeightFromFont(commons.font);
        }

        if (!exitInProgress) {  // Check if we should start the exit process
            if (scenePair.state().isExitSequenceStarted()) {
                exitInProgress = true;
                appearingSuspended = true;
                disappearEnabled = true;
                disappearInterval = 200;
            }
        }

        //region Handle Dirty Scene
        if (scenePair.obj().isDirty() && !exitInProgress) {  // When the scene is dirty, need to un-suspend the algorithm.
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

        if (affectedByLight && shader != null)
            commons.batch.setShader(shader);

        commons.batch.begin();

        if (affectedByLight) {
            if (light.isAttached())
                LightUtils.updateLightToMousePosition(light);

            shader.setUniformf("LightPos", light.getPosition());
        }

        boolean isAppearingInternal = false;    // These are set if at least one fragment is processed, based on those the larger flags are set later
        boolean isDisappearingInternal = false;
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) actorData.get(FragmentId.GLYPH_LAYOUT);
                Rectangle rectangle = (Rectangle) actorData.get(FragmentId.CLICKABLE_AREA);
                Vector2 position = (Vector2) actorData.get(FragmentId.POSITION);
                Integer line = (Integer) actorData.get(FragmentId.LINE);
                Color color = (Color) actorData.get(FragmentId.COLOR);
                Map<String, Boolean> stateFlags = (Map<String, Boolean>) actorData.get(FragmentId.STATE_FLAGS);
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
            } else {
                disappearingSuspended = true;
                if (exitInProgress) {
                    scenePair.state().setExitSequenceFinished(true);
                    reset();
                    finalizeRender();
                    return;
                }
            }

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

        finalizeRender();
    }

    private void finalizeRender() {
        commons.batch.end();

        if (affectedByLight && shader != null)
            commons.batch.setShader(commons.defaultShader);
    }

    @Override
    public void preFirstRender(StatefulScene statefulScene) {
        if (affectedByLight) {
            shader = ShaderUtils.loadShader("singleLight.frag", "basic.vert");

            // Get the first light
            // TODO: Make this use all the lights
            light = lights.get(0);

            if (light == null)
                throw new GdxRuntimeException("Cannot use affected by light text rendering without a light attached");

            AmbientLight ambientLight = commons.ambientLight;

            shader.begin();
            shader.setUniformf("LightColor", light.getColor().x, light.getColor().y, light.getColor().z, light.getIntensity());
            if (ambientLight != null)
                shader.setUniformf("AmbientColor", ambientLight.getColor().x, ambientLight.getColor().y, ambientLight.getColor().z, ambientLight.getIntensity());
            shader.setUniformf("Falloff", light.getFalloff());
            shader.setUniformf("Resolution", commons.worldDimensions.x, commons.worldDimensions.y);
            shader.end();
        }
    }
}
