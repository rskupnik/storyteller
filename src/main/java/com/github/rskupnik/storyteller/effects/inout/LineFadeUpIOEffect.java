package com.github.rskupnik.storyteller.effects.inout;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.rskupnik.storyteller.accessors.ColorAccessor;
import com.github.rskupnik.storyteller.accessors.Vector2Accessor;
import com.github.rskupnik.storyteller.core.scenetransform.Fragment;
import com.github.rskupnik.storyteller.core.sceneextend.ExtenderChain;
import com.github.rskupnik.storyteller.core.sceneextend.ColorToTransparentExtender;
import com.github.rskupnik.storyteller.core.sceneextend.LineExtender;
import com.github.rskupnik.storyteller.core.sceneextend.PullDownExtender;
import com.github.rskupnik.storyteller.peripheral.Actor;
import com.github.rskupnik.storyteller.core.scenetransform.TransformedScene;
import com.github.rskupnik.storyteller.wrappers.pairs.ScenePair;
import org.javatuples.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeUpIOEffect extends IOEffect {

    private final TweenEquation equation;
    private final int duration;
    private final int lingerTime;

    private boolean inProgress = false;     // Determines if any line is being tweened right now
    private boolean finished = false;       // Determines if the whole text is visible and done tweening
    private int currentlyProcessedLine = 1;
    private long timestamp = 0;             // Needed to line the effects one after another
    private Map<Integer, Float> linePosition = new HashMap<>();

    public LineFadeUpIOEffect(TweenEquation equation, int duration, int lingerTime) {
        super(ExtenderChain.from(new LineExtender(), new ColorToTransparentExtender(), new PullDownExtender()));
        this.equation = equation;
        this.duration = duration;
        this.lingerTime = lingerTime;
    }

    @Override
    public void render(float delta, SpriteBatch batch, BitmapFont font, TweenManager tweenManager, ScenePair scenePair) {
        if (font == null || batch == null)
            return; // Throw exception?

        TransformedScene data = scenePair.internal().getTransformedScene();
        if (data == null)
            return;

        if (inProgress && System.currentTimeMillis() > timestamp) {
            inProgress = false;
            currentlyProcessedLine++;
        }

        boolean atLeastOneProcessed = false;
        boolean shouldStop = true;
        for (Pair<Actor, List<Fragment>> actorToDataPair : data.getData()) {
            Actor actor = actorToDataPair.getValue0();
            for (Fragment actorData : actorToDataPair.getValue1()) {
                // Unpack data
                GlyphLayout GL = (GlyphLayout) actorData.get("glyphLayout");
                Rectangle rectangle = (Rectangle) actorData.get("clickableArea");
                Vector2 position = (Vector2) actorData.get("position");
                Integer line = (Integer) actorData.get("line");
                Color color = (Color) actorData.get("color");

                if (GL == null || position == null)
                    continue;

                if (linePosition.get(line) == null) {
                    linePosition.put(line, position.y);
                }

                if (finished) {
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                } else {  // Only apply the algorithm if not yet finished
                    if (!inProgress && line == currentlyProcessedLine) {    // Should start tweening this line
                        atLeastOneProcessed = true;

                        Timeline sequence = Timeline.createSequence();
                        sequence.beginParallel()
                                .push(
                                    Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                        .target(position.y + 5)
                                        .ease(equation)
                                ).push(
                                    Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                        .target(1.0f)
                                        .ease(equation)
                                )
                                .end();
                        if (lingerTime > 0) {
                            int i = line;
                            while (i > 1) {
                                sequence.push(
                                        Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                                .target(linePosition.get(i - 1))
                                                .ease(equation)
                                        //.delay(lingerTime / 1000f)
                                );
                                i--;
                            }
                            sequence.beginParallel()
                                    .push(
                                            Tween.to(position, Vector2Accessor.Y, duration / 1000f)
                                                    .target(linePosition.get(1) + 5.0f)
                                                    .ease(equation)
                                                    .delay(lingerTime / 1000f)
                                    )
                                    .push(
                                            Tween.to(color, ColorAccessor.ALPHA, duration / 1000.0f)
                                                    .target(0f)
                                                    .ease(equation)
                                                    .delay(lingerTime / 1000f)
                                    )
                                    .end();
                        }
                        sequence.start(tweenManager);
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
