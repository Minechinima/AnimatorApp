package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;
import com.minechinima.model.api.IData;
import com.minechinima.model.events.types.ChangeEvent;

/**
 * Functional interface for a custom tagging strategy that is invoked when a {@link ChangeEvent}
 * is dispatched on watched data.
 * The function will be executed within the context of a node.
 * <br><br>
 * Important: the mapping of a listener to a datanode ensures the listener can be cleaned when the node
 * is removed from the tree.
 * @param <T> the type of dependency graph node this listener is associated with
 */
@FunctionalInterface
public interface NodeTaggingStrategy<T extends Node> {
    /**
     * Called when a relevant change event fires on watched data.
     *
     * @param graph  the dependency graph that owns the node – use for tagging operations
     * @param event  the change event that was dispatched
     * @param data   the {@link IData} instance the event bus belongs to
     * @param node   the dependency graph node associated with this listener
     */
    void onEvent(DepsGraph graph, ChangeEvent<?> event, IData data, T node);
}

