package com.minechinima.depsgraph.builder;


import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;

/**
 * Strategy for building relations between nodes.
 * This is separated from the component and operation builders, because when building a component or operation,
 * the dependency graph is not finished yet and the other nodes are not available.
 * Therefore this builder gets access to the dependency graph and can build relations to other nodes.
 * @param <T>
 */
@FunctionalInterface
public interface BufferedBuilderFunc<T extends Node> {
    void buildRelations(T node, BufferedBuilderHelper helper);
}
