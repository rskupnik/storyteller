package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.background.initializers.NormalMappedBackgroundInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import javax.inject.Inject;
import java.io.*;
import java.util.Scanner;

public class NormalMappedBackgroundRenderingUnit extends BackgroundRenderingUnit {

    public static final float DEFAULT_LIGHT_Z = 0.075f;
    public static final float AMBIENT_INTENSITY = 0.2f;
    public static final float LIGHT_INTENSITY = 1f;

    public static final Vector3 LIGHT_POS = new Vector3(100f,100f,DEFAULT_LIGHT_Z);
    public static final Vector3 LIGHT_COLOR = new Vector3(1f, 0.22f, 0.0f);
    public static final Vector3 AMBIENT_COLOR = new Vector3(0.2f, 0.2f, 0.2f);
    public static final Vector3 FALLOFF = new Vector3(.4f, 3f, 20f);

    @Inject
    Commons commons;

    private Texture normalMap;
    private ShaderProgram shader;
    private ShaderProgram defaultShader;

    @Inject
    public NormalMappedBackgroundRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {
        NormalMappedBackgroundInitializer NMBInitializer = (NormalMappedBackgroundInitializer) initializer;
        this.normalMap = NMBInitializer.getNormalMap();

        InputStream fragFile = getClass().getClassLoader().getResourceAsStream("normalMapShader.frag");
        String fragFileContents = getFileContents(fragFile);

        InputStream vertFile = getClass().getClassLoader().getResourceAsStream("normalMapShader.vert");
        String vertFileContents = getFileContents(vertFile);

        ShaderProgram.pedantic = false;
        shader = new ShaderProgram(vertFileContents, fragFileContents);

        // Ensure it compiled
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());

        // Print any warnings
        if (shader.getLog().length() != 0)
            System.out.println(shader.getLog());

        defaultShader = commons.batch.createDefaultShader();

        // Setup default uniforms
        shader.begin();
        shader.setUniformi("u_normals", 1); // GL_TEXTURE1 - normal map
        shader.setUniformf("LightColor", LIGHT_COLOR.x, LIGHT_COLOR.y, LIGHT_COLOR.z, LIGHT_INTENSITY);
        shader.setUniformf("AmbientColor", AMBIENT_COLOR.x, AMBIENT_COLOR.y, AMBIENT_COLOR.z, AMBIENT_INTENSITY);
        shader.setUniformf("Falloff", FALLOFF);
        shader.setUniformf("Resolution", 800, 600);
        shader.end();
    }

    @Override
    public void render(float delta, StatefulStage statefulStage) {
        SpriteBatch batch = commons.batch;
        Texture background = statefulStage.obj().getBackgroundImage();
        if (background == null)
            return;

        Rectangle stageBounds = statefulStage.obj().getRectangle();

        batch.setShader(shader);
        batch.begin();

        // Update light position
        float x = (float) Gdx.input.getX() / (float)Gdx.graphics.getWidth();
        float y = ((float)Gdx.graphics.getHeight() - (float)Gdx.input.getY()) / (float)Gdx.graphics.getHeight();

        LIGHT_POS.x = x;
        LIGHT_POS.y = y;

        shader.setUniformf("LightPos", LIGHT_POS);
        normalMap.bind(1);
        background.bind(0);
        batch.draw(background, stageBounds.x, stageBounds.y, stageBounds.getWidth(), stageBounds.getHeight());

        batch.end();
        batch.setShader(defaultShader);
    }

    private String getFileContents(InputStream file) {
        StringBuilder result = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(file))) {

            String line = null;
            while ((line = br.readLine()) != null) {
                result.append(line).append("\n");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }
}
