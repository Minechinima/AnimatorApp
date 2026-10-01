package com.minechinima.depsgraph.builder;

import com.minechinima.model.api.IData;

/**
 * Function to add the operation nodes to the component.
 * Called after entry and exit have been added, so internal operations can be built with relations to them.
 */
@FunctionalInterface
public interface OperationNodesBuildFunc<DATA extends T, EVAL extends T, T extends IData> {
    /**
     * Is called after entry and exit have been built so internal operations can be built with relations to them.
     * Adds operation nodes to the component.
     * <br><br>
     * For relations to any other nodes, even nodes in the same data node, the external {@link BufferedBuilderFunc} should be used,
     * because the other nodes might not be built yet when this method is called.
     *
     * @param data the original database value
     */
    void addOperationNodes(DATA data, OperationNodesBuilderHelper<DATA, EVAL, T> helper);
}
