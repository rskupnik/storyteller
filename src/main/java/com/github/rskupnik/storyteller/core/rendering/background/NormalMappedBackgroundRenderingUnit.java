package com.github.rskupnik.storyteller.core.rendering.background;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Lights;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.rendering.RenderingUnit;
import com.github.rskupnik.storyteller.statefulobjects.StatefulScene;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.structs.backgrounds.Background;
import com.github.rskupnik.storyteller.structs.backgrounds.NormalMappedBackground;
import com.github.rskupnik.storyteller.utils.LightUtils;
import com.github.rskupnik.storyteller.utils.ShaderUtils;

import javax.inject.Inject;

public class NormalMappedBackgroundRenderingUnit extends RenderingUnit {

    private static final ShaderProgram shader = ShaderUtils.loadShader("normalMapWithLight.frag", "basic.vert");

    @Inject Commons commons;
    @Inject Lights lights;

    private Light light;

    @Inject
    public NormalMappedBackgroundRenderingUnit() {

    }

    @Override
    public void render(float delta, StatefulStage stage) {
        super.render(delta, stage);

        Background background = stage.obj().getBackground();
        if (background == null)
            return;

        if (!(background instanceof NormalMappedBackground))
            throw new IllegalStateException("Expected a NormalMappedBackground but go a different one!");

        NormalMappedBackground nmBackground = (NormalMappedBackground) background;
        if (!nmBackground.isInitialized()) {
            shader.begin();
            shader.setUniformi("u_normals", 1); // GL_TEXTURE1 - normal map
            shader.setUniformf("Resolution", commons.worldDimensions.x, commons.worldDimensions.y);
            shader.end();
            nmBackground.setInitialized(true);
        }

        Rectangle area = stage.obj().getBackgroundArea();
        if (area == null)
            area = stage.obj().getRectangle();

        SpriteBatch batch = commons.batch;
        batch.setShader(shader);
        batch.begin();

        if (light.isAttached())
            LightUtils.updateLightToMousePosition(light);

        shader.setUniformf("LightPos", light.getPosition());
        nmBackground.getNormals().bind(1);
        nmBackground.getImage().bind(0);
        batch.draw(nmBackground.getImage(), area.x, area.y, area.getWidth(), area.getHeight());

        batch.end();
        batch.setShader(commons.defaultShader);
    }

    @Override
    public void preFirstRender(StatefulStage stage) {
        // Get the first light
        // TODO: Make this use all the lights
        light = lights.get(0);

        if (light == null)
            throw new GdxRuntimeException("Cannot use NormalMappedBackground without a light being set up!");

        AmbientLight ambientLight = commons.ambientLight;

        shader.begin();
        shader.setUniformf("LightColor", light.getColor().x, light.getColor().y, light.getColor().z, light.getIntensity());
        if (ambientLight != null)
            shader.setUniformf("AmbientColor", ambientLight.getColor().x, ambientLight.getColor().y, ambientLight.getColor().z, ambientLight.getIntensity());
        shader.setUniformf("Falloff", light.getFalloff());
        shader.end();
    }
}
