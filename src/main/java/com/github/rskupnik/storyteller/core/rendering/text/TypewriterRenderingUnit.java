package com.github.rskupnik.storyteller.core.rendering.text;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Lights;
import com.github.rskupnik.storyteller.core.effects.ShakeEffect;
import com.github.rskupnik.storyteller.core.effects.ShakeEffectHandler;
import com.github.rskupnik.storyteller.core.effects.TemporaryEffect;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.structs.ids.FragmentId;
import com.github.rskupnik.storyteller.structs.textrenderer.TextRenderer;
import com.github.rskupnik.storyteller.structs.textrenderer.TypewrittenTextRenderer;
import com.github.rskupnik.storyteller.utils.LightUtils;
import com.github.rskupnik.storyteller.utils.ShaderUtils;
import net.dermetfan.gdx.Typewriter;
import org.javatuples.Pair;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterRenderingUnit extends TextRenderingUnit {

    @Inject Commons commons;
    @Inject TweenManager tweenManager;
    @Inject Lights lights;

    private final ShakeEffectHandler shakeEffectHandler = new ShakeEffectHandler();

    private Typewriter typewriter;
    private Map<StatefulActor, List<Integer>> processingMap = new HashMap<>();  // Holds indexes of fragments that have been processed in the scope of an actor
    private Vector2 offset = new Vector2(0, 0);
    private Vector2 noise = new Vector2(0, 0);                  // Holds the noise to be applied to position (for example for a shake effect)
    private boolean shakeActive = false;                        // If true, shake effect will be activated
    private boolean exitInProgress;
    private long exitTimestamp = 0;
    private boolean informedAboutTextFinishedDisplaying;        // Whether the outside user was informed that text has finished displaying

    private boolean affectedByLight;
    private ShaderProgram shader;
    private Light light;

    private int charsPerSecond;
    private float exitDuration = 1.0f;

    @Inject
    public TypewriterRenderingUnit() {

    }

    @Override
    public void init(TextRenderer textRenderer) {
        TypewrittenTextRenderer twtr = (TypewrittenTextRenderer) textRenderer;
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(charsPerSecond = twtr.getCharsPerSecond());

        this.affectedByLight = twtr.isAffectedByLight();
    }

    @Override
    public void reset() {
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(charsPerSecond);
        processingMap.clear();
        offset = new Vector2(0, 0);
        exitInProgress = false;
        exitTimestamp = 0;
        informedAboutTextFinishedDisplaying = false;
    }

    @Override
    public void render(float delta, StatefulStage stage) {
        super.render(delta, stage);

        StatefulScene statefulScene = stage.state().getAttachedScene();

        if (statefulScene == null)
            return;

        if (commons.font == null || commons.batch == null)
            return; // Throw exception?

        if (statefulScene.state().isExitSequenceStarted() && !exitInProgress) {
            exitInProgress = true;
            initExit(statefulScene);
        }

        if (exitInProgress && System.currentTimeMillis() - exitTimestamp > exitDuration * 1000) {
            statefulScene.state().setExitSequenceFinished(true);
            reset();
            return;
        }

        if (affectedByLight && shader != null)
            commons.batch.setShader(shader);

        commons.batch.begin();

        if (affectedByLight) {
            if (light.isAttached())
                LightUtils.updateLightToMousePosition(light);

            shader.setUniformf("LightPos", light.getPosition());
        }

        //region Temporary Effects
        TemporaryEffect temporaryEffect = stage.obj().getTemporaryEffect();
        if (temporaryEffect != null) {
            activateTemporaryEffect(temporaryEffect, stage);
        }
        //endregion

        //region Shake
        if (shakeActive) {
            noise = shakeEffectHandler.update(delta);
            if (noise == null) {    // If return value is null, it means the duration has ended, so we should disable the effect
                shakeActive = false;
            }
        }
        //endregion

        TransformedScene data = statefulScene.state().getTransformedScene();
        /*
            The algorithm here is as follows:
            - hold the index of the actor being iterated in variable i
            - if currentlyProcessedActorIndex is -1, it means no actor is processed currently
            - we choose the first unprocessed actor we find and make it the currently processed one
            - all actors that are already processed simply display their text
            - the actor that is being processed, displays his text using a resetted typewriter
            - all actors that are not processed but are not the currently processed one, are ignored
            - the same happens inside every actor for his list of CharSequences
         */
        int i = 0;  // Index of the current actor from the actor list
        int currentlyProcessedActorIndex = -1;  // Index of the actor that is currently processed
        boolean allActorsProcessed = true;      // Will be set to false if any fragment of any actor is not processed - based on this we informed about job done or not
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            if (currentlyProcessedActorIndex == -1) {   // If no actor is being processed, set it to this one
                currentlyProcessedActorIndex = i;
            }

            // If actor is not yet processed and is not the one currently processed, ignore
            if (!actor.state().isProcessed() && i != currentlyProcessedActorIndex) {
                i++;
                allActorsProcessed = false;
                continue;
            }

            int currentlyProcessedFragmentIndex = -1;   // Index of the fragment currently being processed (in scope of the actor)
            int j = 0;  // Index of the current fragment from the actor's fragment list
            boolean allFragmentsProcessed = true;   // Set to false if at least one fragment is unprocessed
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                String str = (String) actorData.get(FragmentId.CHAR_SEQUENCE);
                //GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = (Rectangle) actorData.get(FragmentId.CLICKABLE_AREA);
                Vector2 position = (Vector2) actorData.get(FragmentId.POSITION);
                Color color = (Color) actorData.get(FragmentId.COLOR);

                if (str == null || position == null)
                    continue;

                Vector2 noiseAdjust = noise != null ? noise.cpy() : new Vector2(0, 0);

                // Draw the GL
                Color prevColor = commons.font.getColor();
                commons.font.setColor(color != null ? color : prevColor);

                if (isProcessed(actor, j)) {    // If this fragment is already processed, draw it as is
                    commons.font.draw(commons.batch, str, position.x + noiseAdjust.x, position.y + actor.state().getYOffset() + offset.y + noiseAdjust.y);
                    j++;
                    continue;
                } else {
                    allFragmentsProcessed = false;  // Fragment is not processed so falsify the flag
                    allActorsProcessed = false;

                    if (currentlyProcessedFragmentIndex == -1)
                        currentlyProcessedFragmentIndex = j;

                    if (currentlyProcessedFragmentIndex != j) { // If this fragment is not the one being processed right now, ignore
                        j++;
                        continue;
                    }

                    if (!exitInProgress)
                        typewriter.update(delta);

                    CharSequence cs = typewriter.type(str);
                    commons.font.draw(commons.batch, cs, position.x + noiseAdjust.x, position.y + actor.state().getYOffset() + offset.y + noiseAdjust.y);

                    if (cs.length() == str.length()) {  // Check if processing of the fragment is finished
                        List<Integer> actorsProcessedIndices = processingMap.get(actor);
                        if (actorsProcessedIndices == null) {
                            actorsProcessedIndices = new ArrayList<>();
                            processingMap.put(actor, actorsProcessedIndices);
                        }
                        actorsProcessedIndices.add(currentlyProcessedFragmentIndex);

                        currentlyProcessedFragmentIndex = -1;
                        typewriter.getInterpolator().setTime(0);
                    }
                }

                commons.font.setColor(prevColor);

                j++;
            }

            if (allFragmentsProcessed) {    // If all fragments for the actor are processed, reset the pointer to find the next one to process
                actor.state().setProcessed(true);
                currentlyProcessedActorIndex = -1;
            }

            i++;
        }

        commons.batch.end();

        if (affectedByLight && shader != null)
            commons.batch.setShader(commons.defaultShader);

        if (allActorsProcessed && !informedAboutTextFinishedDisplaying) {
            // TODO: inform about text finished displaying
            System.out.println("TEXT FINISHED DISPLAYING");
            informedAboutTextFinishedDisplaying = true;
        }
    }

    private void initExit(StatefulScene scene) {
        Tween.to(offset, Vector2Accessor.Y, exitDuration)
                .target(offset.y + commons.font.getData().lineHeight + commons.font.getData().blankLineScale)
                .ease(Quint.OUT)
                .start(tweenManager);

        TransformedScene data = scene.state().getTransformedScene();
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            for (Fragment actorData : actorToDataPair.getValue1()) {
                Color color = (Color) actorData.get(FragmentId.COLOR);

                Tween.to(color, ColorAccessor.ALPHA, exitDuration)
                        .target(0f)
                        .ease(Quint.OUT)
                        .start(tweenManager);
            }
        }

        exitTimestamp = System.currentTimeMillis();
    }

    @Override
    public void preFirstRender(StatefulStage stage) {
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

    private boolean isProcessed(StatefulActor actor, int index) {
        List<Integer> actorsProcessedIndices = processingMap.get(actor);
        if (actorsProcessedIndices == null) {
            actorsProcessedIndices = new ArrayList<>();
            processingMap.put(actor, actorsProcessedIndices);
        }
        return actorsProcessedIndices.contains(index);
    }

    private void activateTemporaryEffect(TemporaryEffect temporaryEffect, StatefulStage stage) {
        if (temporaryEffect instanceof ShakeEffect) {
            ShakeEffect shakeEffect = (ShakeEffect) temporaryEffect;
            shakeEffectHandler.shake(shakeEffect.getIntensity(), shakeEffect.getDuration());    // Starts the calculations of noise to be applied as shake effect
            stage.obj().toggleEffect(null);   // Once an effect is activated, the toggle needs to be taken down from the stage
            shakeActive = true; // Makes the noise visible
        }
    }
}
