package com.github.rskupnik.storyteller.core.transformation.nodes;

import com.github.rskupnik.storyteller.core.transformation.Transformer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class TransformerNode<I, O> implements Transformer<I, O> {

    private List<TransformerNode> nodes = new ArrayList<>();
    private O data;

    public O transformAndPropagate(I input) {
        this.data = transform(input);
        for (TransformerNode node : nodes) {
            node.transformAndPropagate(data);
        }
        return data;
    }

    public void addNode(TransformerNode branch) {
        this.nodes.add(branch);
    }

    public List<TransformerNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    public O getData() {
        return data;
    }
}
