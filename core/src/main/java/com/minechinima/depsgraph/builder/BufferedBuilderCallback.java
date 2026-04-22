package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;

/**
 * Callback for {@link BufferedBuilderFunc}.
 * Utility class to bind the node to the relation builder callback, so
 * that it does not need to be passed as parameter later on again.
 *
 * @param <T>
 */
public class BufferedBuilderCallback<T extends Node> {
    private final T node;
    private final BufferedBuilderFunc<T> relationBuilder;

    public BufferedBuilderCallback(T node, BufferedBuilderFunc<T> relationBuilder) {
        this.node = node;
        this.relationBuilder = relationBuilder;
    }

    public BufferedBuilderHelper build(DepsGraph graph) {
        BufferedBuilderHelper helper = new BufferedBuilderHelper(graph);
        this.relationBuilder.buildRelations(this.node, helper);
        return helper;
    }
}
