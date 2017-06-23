package com.github.rskupnik.storyteller.effects.inout;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquation;
import aurelienribon.tweenengine.TweenManager;
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

import java.util.List;

/**
 * In order to apply this effect we need to store additional information about
 * which line does each GL belong to.
 */
public final class LineFadeUpIOEffect extends IOEffect {

    private final TweenEquation equation;
    private final int duration;

    private boolean inProgress = false;     // Determines if any line is being tweened right now
    private boolean finished = false;       // Determines if the whole text is visible and done tweening
    private int currentlyProcessedLine = 1;
    private long timestamp = 0;             // Needed to line the effects one after another

    public LineFadeUpIOEffect(TweenEquation equation, int duration) {
        super(ExtenderChain.from(new LineExtender(), new ColorToTransparentExtender(), new PullDownExtender()));
        this.equation = equation;
        this.duration = duration;
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

                if (finished) {
                    font.draw(batch, GL, position.x, position.y + actor.getInternalActor().getYOffset());
                } else {  // Only apply the algorithm if not yet finished
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
