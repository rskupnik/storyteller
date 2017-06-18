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
import java.util.List;

public final class TypewriterAppearEffect extends AppearEffect {

    private List<Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>>> data;
    //private TransformedScene data;
    private Typewriter typewriter;

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

        for (Pair<Actor, ArrayList<Quartet<CharSequence, Rectangle, Vector2, Color>>> actorToDataPair : data) {
            Actor actor = actorToDataPair.getValue0();
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
                font.draw(batch, typewriter.updateAndType(str, delta), position.x, position.y + actor.getInternalActor().getYOffset());
                font.setColor(prevColor);

                // Add the Rectangle to clickables
                // TODO: Maybe this should be done somewhere else instead of render(), since it's a one-time action
                //if (rectangle != null && scenePair.internal().isFirstDraw())
                //    inputHandler.addClickable(scenePair.scene(), rectangle, actor);
            }
        }
    }
}
