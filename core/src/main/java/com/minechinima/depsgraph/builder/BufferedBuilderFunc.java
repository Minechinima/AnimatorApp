package com.minechinima.depsgraph.builder;


import com.minechinima.depsgraph.Node;

/**
 * Builder function that will be executed after all nodes have been built in the graph. This can be used to create relations
 * between the nodes.
 * This is separated from the component and operation builders, because when building a component or operation,
 * the dependency graph is not finished yet and the other nodes are not available.
 * @param <T>
 */
@FunctionalInterface
public interface BufferedBuilderFunc<T extends Node> {
    void build(T node, BufferedBuilderHelper helper);
}
