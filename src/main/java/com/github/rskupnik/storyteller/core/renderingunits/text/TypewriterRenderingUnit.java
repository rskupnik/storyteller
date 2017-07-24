package com.github.rskupnik.storyteller.core.renderingunits.text;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Lights;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.text.initializers.TypewriterInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.CharSequenceExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ColorExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.statefulobjects.StatefulActor;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import javax.inject.Inject;

import com.github.rskupnik.storyteller.utils.FileUtils;
import net.dermetfan.gdx.Typewriter;
import org.javatuples.Pair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterRenderingUnit extends RenderingUnit {

    @Inject Commons commons;
    @Inject TweenManager tweenManager;
    @Inject Lights lights;

    private Typewriter typewriter;
    private Map<StatefulActor, List<Integer>> processingMap = new HashMap<>();  // Holds indexes of fragments that have been processed in the scope of an actor

    private boolean affectedByLight;
    private ShaderProgram shader;
    private Light light;

    @Inject
    public TypewriterRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {
        setChain(ExtenderChain.from(new CharSequenceExtender(), new ColorExtender()));

        TypewriterInitializer initializerTW = (TypewriterInitializer) initializer;
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(initializerTW.getCharsPerSecond());

        this.affectedByLight = initializerTW.isAffectedByLight();
    }

    @Override
    public void render(float delta, StatefulScene statefulScene) {
        super.render(delta, statefulScene);

        if (commons.font == null || commons.batch == null)
            return; // Throw exception?

        if (affectedByLight && shader != null)
            commons.batch.setShader(shader);

        commons.batch.begin();

        if (affectedByLight) {
            // Update light position
            // TODO: Update the light position only in a single place
            if (light.isAttached()) {
                float x = (float) Gdx.input.getX() / (float) Gdx.graphics.getWidth();
                float y = ((float) Gdx.graphics.getHeight() - (float) Gdx.input.getY()) / (float) Gdx.graphics.getHeight();

                light.setPosition(x, y);
            }

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
                String str = (String) actorData.get("charSequence");
                //GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = (Rectangle) actorData.get("clickableArea");
                Vector2 position = (Vector2) actorData.get("position");
                Color color = (Color) actorData.get("color");

                if (str == null || position == null)
                    continue;

                // Draw the GL
                Color prevColor = commons.font.getColor();
                commons.font.setColor(color != null ? color : prevColor);

                if (isProcessed(actor, j)) {    // If this fragment is already processed, draw it as is
                    commons.font.draw(commons.batch, str, position.x, position.y + actor.state().getYOffset());
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

                    CharSequence cs = typewriter.updateAndType(str, delta);
                    commons.font.draw(commons.batch, cs, position.x, position.y + actor.state().getYOffset());

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

    @Override
    public void preFirstRender(StatefulScene statefulScene) {
        if (affectedByLight) {
            // TODO: Extract shader loading logic into a single place
            InputStream fragFile = getClass().getClassLoader().getResourceAsStream("singleLight.frag");
            String fragFileContents = FileUtils.getFileContents(fragFile);

            InputStream vertFile = getClass().getClassLoader().getResourceAsStream("basic.vert");
            String vertFileContents = FileUtils.getFileContents(vertFile);

            ShaderProgram.pedantic = false;
            shader = new ShaderProgram(vertFileContents, fragFileContents);

            // Ensure it compiled
            if (!shader.isCompiled())
                throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());

            // Print any warnings
            if (shader.getLog().length() != 0)
                System.out.println(shader.getLog());

            // TODO: Extract the shader setup into a single place?

            // Get the first light
            // TODO: Make this use all the lights
            light = lights.get(0);

            // TODO: Throw exception or draw a black blackground?
            if (light == null)
                throw new GdxRuntimeException("Cannot use affected by light text rendering without a light attached");

            AmbientLight ambientLight = commons.ambientLight;

            shader.begin();
            shader.setUniformf("LightColor", light.getColor().x, light.getColor().y, light.getColor().z, light.getIntensity());
            if (ambientLight != null)   // TODO: Throw exception if missing instead?
                shader.setUniformf("AmbientColor", ambientLight.getColor().x, ambientLight.getColor().y, ambientLight.getColor().z, ambientLight.getIntensity());
            shader.setUniformf("Falloff", light.getFalloff());
            shader.setUniformf("Resolution", 800, 600); // TODO: De-hardcode this
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
