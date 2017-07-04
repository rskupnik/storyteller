package com.github.rskupnik.storyteller.core.renderingunits;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.initializers.TypewriterInitializer;
import com.github.rskupnik.storyteller.core.sceneextend.CharSequenceExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ColorExtender;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.structs.Fragment;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import com.google.inject.Inject;
import net.dermetfan.gdx.Typewriter;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterRenderingUnit extends RenderingUnit {

    @Inject private Commons commons;
    @Inject private TweenManager tweenManager;

    private Typewriter typewriter;
    private Map<Actor, List<Integer>> processingMap = new HashMap<>();  // Holds indexes of fragments that have been processed in the scope of an actor

    @Override
    public void init(RenderingUnitInitializer initializer) {
        setChain(ExtenderChain.from(new CharSequenceExtender(), new ColorExtender()));

        TypewriterInitializer initializerTW = (TypewriterInitializer) initializer;
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(initializerTW.getCharsPerSecond());
    }

    @Override
    public void render(float delta, ScenePair scenePair) {
        if (commons.font == null || commons.batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.internal().getTransformedScene();
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
        for (Pair<Actor, List<Fragment>> actorToDataPair : data.getData()) {
            Actor actor = actorToDataPair.getValue0();
            if (currentlyProcessedActorIndex == -1) {   // If no actor is being processed, set it to this one
                currentlyProcessedActorIndex = i;
            }

            // If actor is not yet processed and is not the one currently processed, ignore
            if (!actor.getInternalActor().isProcessed() && i != currentlyProcessedActorIndex) {
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
                    commons.font.draw(commons.batch, str, position.x, position.y + actor.getInternalActor().getYOffset());
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
                    commons.font.draw(commons.batch, cs, position.x, position.y + actor.getInternalActor().getYOffset());

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
                actor.getInternalActor().setProcessed(true);
                currentlyProcessedActorIndex = -1;
            }

            i++;
        }
    }

    private boolean isProcessed(Actor actor, int index) {
        List<Integer> actorsProcessedIndices = processingMap.get(actor);
        if (actorsProcessedIndices == null) {
            actorsProcessedIndices = new ArrayList<>();
            processingMap.put(actor, actorsProcessedIndices);
        }
        return actorsProcessedIndices.contains(index);
    }
}
