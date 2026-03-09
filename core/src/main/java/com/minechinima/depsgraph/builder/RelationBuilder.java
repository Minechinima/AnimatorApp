package com.minechinima.depsgraph.builder;


import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;
import com.minechinima.depsgraph.OperationNode;
import com.minechinima.model.base.BaseStructure;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Strategy for building relations between nodes.
 * This is separated from the component and operation builders, because when building a component or operation,
 * the dependency graph is not finished yet and the other nodes are not available.
 * Therefore this builder gets access to the dependency graph and can build relations to other nodes.
 * @param <T>
 */
@FunctionalInterface
public interface RelationBuilder<T extends Node> {
    //TODO add a helper here to allow creating tagging listeners with relations.
    // TODO remove graph from API, only helper with delegation methods -> restrict access so relation builders cannot be
    //  abused to remove nodes from depsgraph.
    // TODO allow creating a tagging listener for a specific node context. The buildRelations method could be used to
    //  build relations between arbitrary nodes.
    void buildRelations(DepsGraph grap, T node);
}
