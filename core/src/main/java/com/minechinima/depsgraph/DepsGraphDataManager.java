package com.minechinima.depsgraph;

import com.minechinima.depsgraph.tagging.NodeTaggingManager;
import com.minechinima.depsgraph.tagging.NodeTaggingManager.TaggingConfigCallback;

import java.util.Collection;

/**
 * Manages one dependency graph and updates it when data changes.
 */
public class DepsGraphDataManager {
    //manage depsgraph
    //manage depsgraph scheduler and updating graph and scheduler when topology changes
    //manage registry and automatically updating graph and tagging

    private final DepsGraph graph;
    private NodeTaggingManager taggingManager;

    public DepsGraphDataManager(DepsGraph graph) {
        this.graph = graph;
        this.taggingManager = new NodeTaggingManager(graph);
    }

    /**
     * Register all tagging config callbacks that were collected during the build process.
     * Should be called after the graph is fully built (nodes + relations).
     *
     * @param callbacks the callbacks binding nodes to their tagging configs
     */
    public void registerTaggingConfigs(Collection<TaggingConfigCallback<?>> callbacks) {
        this.taggingManager.registerAll(callbacks);
    }

    /**
     * Tear down and recreate the tagging manager.
     * Call before rebuilding the graph to prevent stale event-bus subscriptions.
     */
    public void resetTagging() {
        this.taggingManager.unsubscribeAll();
        this.taggingManager = new NodeTaggingManager(this.graph);
    }

    /**
     * Remove a node
     *
     * @param node the node to unregister
     */
    public void removeNode(Node node) {
        this.taggingManager.unregister(node);
    }

    public DepsGraph getGraph() {
        return this.graph;
    }

    public NodeTaggingManager getTaggingManager() {
        return this.taggingManager;
    }
}
