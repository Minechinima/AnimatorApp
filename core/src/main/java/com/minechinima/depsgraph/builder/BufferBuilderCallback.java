package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;

/**
 * Utility class to bind the node to the relation builder callback, so
 * that it does not need to be passed as parameter later on again.
 *
 * @param <T>
 */
public class BufferBuilderCallback<T extends Node> {
    private final T node;
    private final BufferedBuilderFunc<T> relationBuilder;

    public BufferBuilderCallback(T node, BufferedBuilderFunc<T> relationBuilder) {
        this.node = node;
        this.relationBuilder = relationBuilder;
    }

    public void build(DepsGraph graph) {
        this.relationBuilder.buildRelations(graph, this.node);
    }
}
