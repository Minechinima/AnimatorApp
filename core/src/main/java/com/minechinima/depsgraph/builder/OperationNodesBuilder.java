package com.minechinima.depsgraph.builder;

import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

/**
 * Function to add the operation nodes to the component.
 * Called after entry and exit have been added, so internal operations can be built with relations to them.
 */
@FunctionalInterface
public interface OperationNodesBuilder<DATA extends T, EVAL extends T, T extends IData> {
    /**
     * Is called after entry and exit have been built so internal operations can be built with relations to them.
     * Adds operation nodes to the component.
     * <br><br>
     * Internal relations can be built here, e.g. from entry to the first internal operation, between internal operations and from the last internal operation to the exit.
     * For relations to any other nodes, even nodes in the same data node, the external {@link RelationBuilder} should be used,
     * because the other nodes might not be built yet when this method is called.
     *
     * @param data the original database value
     */
    void addOperationNodes(DATA data, ComponentBuilder<DATA, EVAL, T>.OperationNodesBuilderHelper helper);
}
