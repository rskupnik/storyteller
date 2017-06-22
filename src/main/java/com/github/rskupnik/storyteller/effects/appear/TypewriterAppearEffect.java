package com.github.rskupnik.storyteller.effects.appear;

import aurelienribon.tweenengine.TweenManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.core.transformation.TransformationTree;
import com.github.rskupnik.storyteller.core.transformation.nodes.GLToCharSequenceTransformer;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import net.dermetfan.gdx.Typewriter;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterAppearEffect extends AppearEffect {

    //private List<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> data;
    private Typewriter typewriter;
    private Map<Actor, List<Integer>> processingMap = new HashMap<>();  // Holds indexes of fragments that have been processed in the scope of an actor

    public TypewriterAppearEffect() {
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(20);
    }

    @Override
    public Object getData() {
        return null;
    }

    @Override
    public void transform(TransformedScene input) {
        TransformedScene<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>> input2 = (TransformedScene<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>>) input;
        List<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> output = new ArrayList<>();
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : input2) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>> quartetList = new ArrayList<>();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                GlyphLayout GL = actorData.getValue0();
                String str = TextConverter.glyphLayoutToString(new StringBuilder(), GL, false).toString();

                // This assumes the whole GL has one color
                quartetList.add(Quartet.with((CharSequence) str, actorData.getValue1(), actorData.getValue2(), GL.runs.get(0).color));
            }
            output.add(Pair.with(actor, quartetList));
        }
        //this.data = output;
    }

    @Override
    public void render(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, TransformationTree transformationTree) {
        if (font == null || batch == null)
            return; // Throw exception?


        TransformedScene<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> data = ((GLToCharSequenceTransformer) transformationTree.get(GLToCharSequenceTransformer.class)).getData();
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
        for (Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>> actorToDataPair : data) {
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
            for (Quartet<CharSequence, Rectangle, Vector2, Color> actorData : actorToDataPair.getValue1()) {
                // Unpack data
                String str = (String) actorData.getValue0();
                //GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = actorData.getValue1();
                Vector2 position = actorData.getValue2();
                Color color = actorData.getValue3();

                if (str == null || position == null)
                    continue;

                // Draw the GL
                Color prevColor = font.getColor();
                font.setColor(color != null ? color : prevColor);

                if (isProcessed(actor, j)) {    // If this fragment is already processed, draw it as is
                    font.draw(batch, str, position.x, position.y + actor.getInternalActor().getYOffset());
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
                    font.draw(batch, cs, position.x, position.y + actor.getInternalActor().getYOffset());

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

                font.setColor(prevColor);

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
