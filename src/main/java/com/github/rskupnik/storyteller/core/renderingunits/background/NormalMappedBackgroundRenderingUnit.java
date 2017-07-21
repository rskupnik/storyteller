package com.github.rskupnik.storyteller.core.renderingunits.background;

import com.badlogic.gdx.graphics.Texture;
import com.github.rskupnik.storyteller.aggregates.Commons;
import com.github.rskupnik.storyteller.core.renderingunits.RenderingUnitInitializer;
import com.github.rskupnik.storyteller.core.renderingunits.background.initializers.NormalMappedBackgroundInitializer;
import com.github.rskupnik.storyteller.statefulobjects.StatefulStage;

import javax.inject.Inject;
import java.io.*;
import java.util.Scanner;

public class NormalMappedBackgroundRenderingUnit extends BackgroundRenderingUnit {

    @Inject
    Commons commons;

    private Texture normalMap;

    @Inject
    public NormalMappedBackgroundRenderingUnit() {

    }

    @Override
    public void init(RenderingUnitInitializer initializer) {
        NormalMappedBackgroundInitializer NMBInitializer = (NormalMappedBackgroundInitializer) initializer;
        this.normalMap = NMBInitializer.getNormalMap();

        InputStream fragFile = getClass().getClassLoader().getResourceAsStream("normalMapShader.frag");
        String fragFileContents = getFileContents(fragFile);
        System.out.println("FRAG FILE: ");
        System.out.println(fragFileContents);
    }

    @Override
    public void render(float delta, StatefulStage statefulStage) {

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
