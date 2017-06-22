package com.github.rskupnik.storyteller.core.transformation;

import com.github.rskupnik.storyteller.core.transformation.nodes.SceneTransformer;
import com.github.rskupnik.storyteller.core.transformation.nodes.ExtractColorTransformer;
import com.github.rskupnik.storyteller.core.transformation.nodes.GLToCharSequenceTransformer;
import com.github.rskupnik.storyteller.core.transformation.nodes.TransformerNode;
import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public final class TransformationTree {

    private Map<Class, TransformerNode> transformers = new HashMap<>();

    @Inject
    public TransformationTree(SceneTransformer basicTransformer) {
        transformers.put(SceneTransformer.class, basicTransformer);
        add(new ExtractColorTransformer(), SceneTransformer.class);
        add(new GLToCharSequenceTransformer(), ExtractColorTransformer.class);
    }

    public void add(TransformerNode transformer, Class parentClass) {
        if (transformers.get(transformer.getClass()) != null)
            throw new IllegalArgumentException("There already is a transformer of this class registered: "+transformer.getClass());

        TransformerNode parent = transformers.get(parentClass);
        if (parent == null)
            throw new IllegalArgumentException("Cannot attach to a non-existing parent: "+parentClass);

        parent.addNode(transformer);
        transformers.put(transformer.getClass(), transformer);
    }

    public TransformerNode get(Class clazz) {
        return transformers.get(clazz);
    }
}
