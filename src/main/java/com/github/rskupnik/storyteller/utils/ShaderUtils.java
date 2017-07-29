package com.github.rskupnik.storyteller.utils;

import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.utils.GdxRuntimeException;

import java.io.InputStream;

public class ShaderUtils {

    public static ShaderProgram loadShader(String frag, String vert) {
        InputStream fragFile = ShaderUtils.class.getClassLoader().getResourceAsStream(frag);
        String fragFileContents = FileUtils.getFileContents(fragFile);

        InputStream vertFile = ShaderUtils.class.getClassLoader().getResourceAsStream(vert);
        String vertFileContents = FileUtils.getFileContents(vertFile);

        ShaderProgram.pedantic = false;
        ShaderProgram shader = new ShaderProgram(vertFileContents, fragFileContents);

        // Ensure it compiled
        if (!shader.isCompiled())
            throw new GdxRuntimeException("Could not compile shader: "+shader.getLog());

        // Print any warnings
        if (shader.getLog().length() != 0)
            System.out.println(shader.getLog());

        return shader;
    }
}
