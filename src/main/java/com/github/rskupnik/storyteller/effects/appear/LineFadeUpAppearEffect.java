package com.github.rskupnik.storyteller.effects.appear;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quint;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ActorAccessor;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.utils.TextConverter;
import com.github.rskupnik.storyteller.wrappers.complex.TransformedScene;
import org.javatuples.Pair;
import org.javatuples.Quartet;
import org.javatuples.Quintet;
import org.javatuples.Triplet;

import java.util.ArrayList;
import java.util.List;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeUpAppearEffect extends AppearEffect {

    private final TweenEquation equation;
    private final int duration;

    private List<Pair<Actor, ArrayList<Quintet<GlyphLayout, Rectangle, Vector2, Integer, Color>>>> data;

    private boolean inProgress = false;     // Determines if any line is being tweened right now
    private boolean finished = false;       // Determines if the whole text is visible and done tweening
    private int currentlyProcessedLine = 1;
    private long timestamp = 0;             // Needed to line the effects one after another

    public LineFadeUpAppearEffect(TweenEquation equation, int duration) {
        this.equation = equation;
        this.duration = duration;
    }

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
        List<Pair<Actor, ArrayList<Quintet<GlyphLayout, Rectangle, Vector2, Integer, Color>>>> output = new ArrayList<>();
        int line = 1;
        float lastX = 0;
        for (Pair<Actor, ArrayList<Triplet<GlyphLayout, Rectangle, Vector2>>> actorToDataPair : input.getData()) {
            Actor actor = actorToDataPair.getValue0();
            ArrayList<Quintet<GlyphLayout, Rectangle, Vector2, Integer, Color>> quintetList = new ArrayList<>();
            for (Triplet<GlyphLayout, Rectangle, Vector2> actorData : actorToDataPair.getValue1()) {
                GlyphLayout GL = actorData.getValue0();
                Vector2 pos = actorData.getValue2();
                Color color = GL.runs.get(0).color;

                if (pos.x <= lastX) {
                    line++;
                }
                lastX = pos.x;

                pos.y -= 5;
                color.a = 0;

                quintetList.add(Quintet.with(GL, actorData.getValue1(), actorData.getValue2(), line, color));
            }
            output.add(Pair.with(actor, quintetList));
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
        for (Pair<Actor, ArrayList<Quintet<GlyphLayout, Rectangle, Vector2, Integer, Color>>> actorToDataPair : data) {
            Actor actor = actorToDataPair.getValue0();
            for (Quintet<GlyphLayout, Rectangle, Vector2, Integer, Color> actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = actorData.getValue0();
                Rectangle rectangle = actorData.getValue1();
                Vector2 position = actorData.getValue2();
                Integer line = actorData.getValue3();
                Color color = actorData.getValue4();

                if (GL == null || position == null)
                    continue;

                if (finished)
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                else {  // Only apply the algorithm if not yet finished
                    if (!inProgress && line == currentlyProcessedLine) {    // Should start tweening this line
                        Tween.to(position, Vector2Accessor.Y, duration / 1000.0f)
                                .target(position.y + 5)
                                .ease(equation)
                                .start(tweenManager);
                        Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                .target(1.0f)
                                .ease(equation)
                                .start(tweenManager);
                        atLeastOneProcessed = true;
                    }

                    if (line == currentlyProcessedLine)     // Will make the algorithm stop if we move beyond the number of available lines
                        shouldStop = false;

                    if (line <= currentlyProcessedLine)
                        font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                }
            }
        }

        if (atLeastOneProcessed) {
            timestamp = System.currentTimeMillis() + duration;
            inProgress = true;
        }

        if (shouldStop)
            finished = true;
    }
}
