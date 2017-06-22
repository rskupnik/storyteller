package com.github.rskupnik.storyteller.core.transformation.nodes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;

public final class GLToCharSequenceTransformer extends TransformerNode<
        TransformedScene<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>>>,
        TransformedScene<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>>> {

    @Override
    public TransformedScene<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> transform(
            TransformedScene<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>>> input) {
        TransformedScene<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> output = new TransformedScene<>();
        for (Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>> actorToDataPair : input) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>> quartetList = new ArrayList<>();
            for (Quartet<GlyphLayout, Rectangle, Vector2, Color> actorData : actorToDataPair.getValue1()) {
                GlyphLayout GL = actorData.getValue0();
                String str = TextConverter.glyphLayoutToString(new StringBuilder(), GL, false).toString();

                // This assumes the whole GL has one color
                quartetList.add(Quartet.with((CharSequence) str, actorData.getValue1(), actorData.getValue2(), actorData.getValue3()));
            }
            output.add(Pair.with(actor, quartetList));
        }
        return output;
    }
}
