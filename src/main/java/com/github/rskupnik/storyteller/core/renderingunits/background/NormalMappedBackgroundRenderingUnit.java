package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.aggregates.Lights;
import com.github.rskupnik.storyteller.core.lighting.AmbientLight;
import com.github.rskupnik.storyteller.core.lighting.Light;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.background.initializers.NormalMappedBackgroundInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;
import com.github.rskupnik.storyteller.utils.FileUtils;
import com.github.rskupnik.storyteller.utils.LightUtils;
import com.github.rskupnik.storyteller.utils.ShaderUtils;

import javax.inject.Inject;
import java.io.*;

public class NormalMappedBackgroundRenderingUnit extends BackgroundRenderingUnit {

    /*public static final float DEFAULT_LIGHT_Z = 0.075f;
    public static final float AMBIENT_INTENSITY = 0.2f;
    public static final float LIGHT_INTENSITY = 1f;

    public static final Vector3 LIGHT_POS = new Vector3(100f,100f,DEFAULT_LIGHT_Z);
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.22f, 0.0f);
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.2f, 0.2f, 0.2f);
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);*/

    @Inject Commons commons;
    @Inject Lights lights;

    private Texture normalMap;
    private ShaderProgram shader;
    private Light light;

    @Inject
    public NormalMappedBackgroundRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {
        NormalMappedBackgroundInitializer NMBInitializer = (NormalMappedBackgroundInitializer) initializer;
        this.normalMap = NMBInitializer.getNormalMap();

        shader = ShaderUtils.loadShader("normalMapWithLight.frag", "basic.vert");

        // Setup default uniforms
        shader.begin();
        shader.setUniformi("u_normals", 1); // GL_TEXTURE1 - normal map
        shader.setUniformf("Resolution", commons.worldDimensions.x, commons.worldDimensions.y);
        shader.end();
    }

    @Override
    public void render(float delta, StatefulStage statefulStage) {
        super.render(delta, statefulStage);

        SpriteBatch batch = commons.batch;
        Texture background = statefulStage.obj().getBackgroundImage();
        if (background == null)
            return;

        Rectangle stageBounds = statefulStage.obj().getRectangle();

        batch.setShader(shader);
        batch.begin();

        if (light.isAttached())
            LightUtils.updateLightToMousePosition(light);

        shader.setUniformf("LightPos", light.getPosition());
        normalMap.bind(1);
        background.bind(0);
        batch.draw(background, stageBounds.x, stageBounds.y, stageBounds.getWidth(), stageBounds.getHeight());

        batch.end();
        batch.setShader(commons.defaultShader);
    }

    @Override
    public void preFirstRender(StatefulStage statefulStage) {
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
