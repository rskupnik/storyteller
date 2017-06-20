package com.github.rskupnik.storyteller.effects.appear;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeUpAppearEffect extends AppearEffect {

    private List<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Integer>>>> data;

    private boolean inProgress = false;
    private boolean finished = false;
    private int currentlyProcessedLine = 1;
    private long timestamp = 0;

    @Override
    public Object getData() {
        return data;
    }

    /**
     * Algorithm to extract line numbers is simple:
     * Simply look at what x does each GL start and if it's lower than previous
     * then we have a new line
     */
    @Override
    public void transform(TransformedScene input) {
        List<Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Integer>>>> output = new ArrayList<>();
        int line = 1;
        float lastX = 0;
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : input.getData()) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Integer>> quartetList = new ArrayList<>();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                GlyphLayout GL = actorData.getValue0();
                Vector2 pos = actorData.getValue2();
                if (pos.x <= lastX) {
                    line++;
                }
                lastX = pos.x;
                pos.y -= 5;
                quartetList.add(Quartet.with(GL, actorData.getValue1(), actorData.getValue2(), line));
                System.out.println(line+": "+GL.toString());
            }
            output.add(Pair.with(actor, quartetList));
        }
        this.data = output;
    }

    @Override
    public void render(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager) {
        if (font == null || batch == null)
            return; // Throw exception?

        if (inProgress && System.currentTimeMillis() > timestamp) {
            inProgress = false;
            currentlyProcessedLine++;
        }

        boolean atLeastOneProcessed = false;
        boolean shouldStop = true;
        for (Pair<Actor, ArrayList<Quartet<GlyphLayout, Rectangle, Vector2, Integer>>> actorToDataPair : data) {
            Actor actor = actorToDataPair.getValue0();
            for (Quartet<GlyphLayout, Rectangle, Vector2, Integer> actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = actorData.getValue1();
                Vector2 position = actorData.getValue2();
                Integer line = actorData.getValue3();

                if (GL == null || position == null)
                    continue;

                if (finished)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                else {
                    if (!inProgress && line == currentlyProcessedLine) {    // Should start tweening
                        Tween.to(position, Vector2Accessor.Y, 1.0f)
                                .target(position.y + 5)
                                .ease(Quad.OUT)
                                .start(tweenManager);
                        atLeastOneProcessed = true;
                    }

                    if (line == currentlyProcessedLine)
                        shouldStop = false;

                    if (line <= currentlyProcessedLine)
                        font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                }
            }
        }

        if (atLeastOneProcessed) {
            timestamp = System.currentTimeMillis() + 1000;
            inProgress = true;
        }

        if (shouldStop)
            finished = true;
    }
}
