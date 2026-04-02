package com.minechinima.depsgraph;

import com.minechinima.depsgraph.builder.DepsGraphBuilderRegistry;
import com.minechinima.depsgraph.tagging.NodeTaggingManager.NodeTaggingCallback;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Manages the dependency graph. Dependeny Graph on its own is stupid, it is only a datastructure with public API to modify it
 * and memory management.
 * This manager object encapsuates different managers, ensures graph stays consistent and is updated on data changes or tagging.
 */
public class DepsGraphDataManager {
    //manage depsgraph
    //manage depsgraph scheduler and updating graph and scheduler when topology changes
    //manage registry and automatically updating graph and tagging

    protected final DepsGraph graph;

    protected final DepsGraphBuilderRegistry builderRegistry;

    public DepsGraphDataManager(DepsGraph graph, DepsGraphBuilderRegistry builderRegistry) {
        this.graph = graph;
        this.builderRegistry = builderRegistry;
    }

    /**
     * Register all tagging config callbacks that were collected during the build process.
     * Should be called after the graph is fully built (nodes + relations).
     *
     * @param callbacks the callbacks binding nodes to their tagging configs
     */
    public void registerTaggingStrategies(Collection<NodeTaggingCallback<?>> callbacks) {

    }

    /**
     * Remove a node
     *
     * @param node the node to unregister
     */
    protected void removeNode(DataNode node) {
        this.graph.removeDataNode(node);
    }

    @Nullable
    public <DATA extends T, T extends IData> T getEvaluatedData(DATA data, Class<T> superType) {
        return this.graph.getEvaluatedData(data, superType);
    }
}
