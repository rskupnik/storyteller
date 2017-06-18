package com.github.rskupnik.storyteller.effects.appear;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import com.google.inject.Inject;
import net.dermetfan.gdx.Typewriter;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class TypewriterAppearEffect extends AppearEffect {

    private List<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> data;
    //private TransformedScene data;
    private Typewriter typewriter;
    private Map<Actor, List<Integer>> processingMap = new HashMap<>();

    public TypewriterAppearEffect() {
        typewriter = new Typewriter();
        typewriter.getAppender().set(new CharSequence[] {""}, new float[] {0});
        typewriter.setCharsPerSecond(2);
    }

    @Override
    public Object getData() {
        return data;
    }

    @Override
    public void transform(TransformedScene input) {
        //this.data = input;
        List<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> output = new ArrayList<>();
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : input.getData()) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>> quartetList = new ArrayList<>();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                GlyphLayout GL = actorData.getValue0();
                String str = TextConverter.glyphLayoutToString(new StringBuilder(), GL, false).toString();
                System.out.println(str);
                // This assumes the whole GL has one color
                quartetList.add(Quartet.with((CharSequence) str, actorData.getValue1(), actorData.getValue2(), GL.runs.get(0).color));
            }
            output.add(Pair.with(actor, quartetList));
        }
        this.data = output;
    }

    @Override
    public void render(float delta, SpriteBatch batch, BitmapFont font) {
        if (font == null || batch == null)
            return; // Throw exception?

        // Start typewriting the first element and set a flag
        // If the flag is set, all further elements are ignored
        // After typewriting is done, set another flag pointing that this actor is typewritten already
        // A typewritten actor should simple be printed without a typewriter
        // The next in line should start being typewritten and the flag needs to be set again
        // Any further actors need to be ignored - until the one being typewritten is finished, the flag is reset, and so on...
        int i = 0;
        int currentlyProcessedActorIndex = -1;
        for (Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>> actorToDataPair : data) {
            Actor actor = actorToDataPair.getValue0();
            if (currentlyProcessedActorIndex == -1) {
                currentlyProcessedActorIndex = i;
            }

            if (!actor.getInternalActor().isProcessed() && i != currentlyProcessedActorIndex) {
                i++;
                continue;
            }
            int currentlyProcessedFragmentIndex = -1;
            int j = 0;
            boolean allFragmentsProcessed = true;
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

                if (isProcessed(actor, j)) {
                    font.draw(batch, str, position.x, position.y + actor.getInternalActor().getYOffset());
                    j++;
                    continue;
                } else {
                    allFragmentsProcessed = false;

                    if (currentlyProcessedFragmentIndex == -1)
                        currentlyProcessedFragmentIndex = j;

                    if (currentlyProcessedFragmentIndex != j) {
                        j++;
                        continue;
                    }

                    CharSequence cs = typewriter.updateAndType(str, delta);
                    font.draw(batch, cs, position.x, position.y + actor.getInternalActor().getYOffset());

                    if (cs.length() == str.length()) {
                        //actor.getInternalActor().setProcessed(true);
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

                // Add the Rectangle to clickables
                // TODO: Maybe this should be done somewhere else instead of render(), since it's a one-time action
                //if (rectangle != null && scenePair.internal().isFirstDraw())
                //    inputHandler.addClickable(scenePair.scene(), rectangle, actor);
            }

            if (allFragmentsProcessed) {
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
