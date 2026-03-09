package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;
import com.minechinima.model.api.IData;
import com.minechinima.model.events.types.ChangeEvent;

/**
 * Functional interface for a custom tagging listener that is invoked when a {@link ChangeEvent}
 * is dispatched on watched data.
 * <br><br>
 * This is the "Level 3" (most flexible) tagging mechanism. Levels 1 and 2 produce default
 * implementations of this interface internally.
 *
 * @param <T> the type of dependency graph node this listener is associated with
 */
@FunctionalInterface
public interface NodeTaggingListener<T extends Node> {
    /**
     * Called when a relevant change event fires on watched data.
     *
     * @param graph  the dependency graph that owns the node – use for tagging operations
     * @param event  the change event that was dispatched
     * @param source the {@link IData} instance the event bus belongs to (cached by the manager)
     * @param node   the dependency graph node associated with this listener
     */
    void onEvent(DepsGraph graph, ChangeEvent<?> event, IData source, T node);
}

