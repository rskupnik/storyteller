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
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.text.RenderingUnit;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.TypewriterInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.CharSequenceExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ColorExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
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

    private Typewriter typewriter;
    private Map<StatefulActor, List<Integer>> processingMap = new HashMap<>();  // Holds indexes of fragments that have been processed in the scope of an actor
    private Vector2 offset = new Vector2(0, 0);
    private boolean exitInProgress;
    private long exitTimestamp = 0;

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
        for (Pair<StatefulActor, List<Fragment>> actorToDataPair : data.getData()) {
            StatefulActor actor = actorToDataPair.getValue0();
            if (currentlyProcessedActorIndex == -1) {   // If no actor is being processed, set it to this one
                currentlyProcessedActorIndex = i;
            }

            // If actor is not yet processed and is not the one currently processed, ignore
            if (!actor.state().isProcessed() && i != currentlyProcessedActorIndex) {
                i++;
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

                // Draw the GL
                Color prevColor = commons.font.getColor();
                commons.font.setColor(color != null ? color : prevColor);

                if (isProcessed(actor, j)) {    // If this fragment is already processed, draw it as is
                    commons.font.draw(commons.batch, str, position.x, position.y + actor.state().getYOffset() + offset.y);
                    j++;
                    continue;
                } else {
                    allFragmentsProcessed = false;  // Fragment is not processed so falsify the flag

                    if (currentlyProcessedFragmentIndex == -1)
                        currentlyProcessedFragmentIndex = j;

                    if (currentlyProcessedFragmentIndex != j) { // If this fragment is not the one being processed right now, ignore
                        j++;
                        continue;
                    }

                    if (!exitInProgress)
                        typewriter.update(delta);

                    CharSequence cs = typewriter.type(str);
                    commons.font.draw(commons.batch, cs, position.x, position.y + actor.state().getYOffset() + offset.y);

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
}
