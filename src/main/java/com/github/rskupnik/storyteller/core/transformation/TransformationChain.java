package com.github.rskupnik.storyteller.core.transformation;

import java.util.HashMap;
import java.util.Map;

public final class TransformationChain {

    private Map<Class, ChainedTransformer> chain = new HashMap<>();
    private ChainedTransformer last;

    public TransformationChain() {
        BasicTransformer basicTransformer = new BasicTransformer();
        last = basicTransformer;
        chain.put(BasicTransformer.class, basicTransformer);
    }

    public void add(ChainedTransformer transformer) {
        if (chain.get(transformer.getClass()) != null)
            throw new IllegalArgumentException("There already is a transformer of this class registered: "+transformer.getClass());

        last.setNext(transformer);
        last = transformer;
        chain.put(transformer.getClass(), transformer);
    }

    public ChainedTransformer get(Class clazz) {
        return chain.get(clazz);
    }
}
