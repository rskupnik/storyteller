package com.github.rskupnik.storyteller.core.transformation;

import com.google.inject.Inject;

import java.util.HashMap;
import java.util.Map;

public final class TransformationTree {

    private Map<Class, TransformerNode> transformers = new HashMap<>();

    @Inject
    public TransformationTree(BasicTransformer basicTransformer) {
        transformers.put(BasicTransformer.class, basicTransformer);
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
