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
import java.util.List;

public class ExtractColorTransformer extends TransformerNode<
        TransformedScene<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>>,
        TransformedScene<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>>>> {

    @Override
    public TransformedScene<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>>> transform(
            TransformedScene<Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>>> input) {
        TransformedScene<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>>>> output = new TransformedScene<>();
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : input) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Color>> quartetList = new ArrayList<>();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                // This assumes the whole GL has one color
                quartetList.add(Quartet.with(actorData.getValue0(), actorData.getValue1(), actorData.getValue2(), actorData.getValue0().runs.get(0).color));
            }
            output.add(Pair.with(actor, quartetList));
        }
        return output;
    }
}
