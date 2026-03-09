package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;
import com.minechinima.eventbus.AbstractEventBus;
import com.minechinima.model.api.IData;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;

import java.util.*;
import java.util.function.Consumer;

/**
 * Manager for the tagging event listeners.
 */
public class NodeTaggingManager {
    private final DepsGraph graph;

    /**
     * Cache: IData → its EventBus (identity-based so every IData instance is unique).
     */
    private final IdentityHashMap<IData, EventBus> eventBusCache = new IdentityHashMap<>();

    /**
     * All subscription tokens we created, so we can unsubscribe in bulk.
     */
    private final List<SubscriptionRecord> subscriptions = new ArrayList<>();

    public NodeTaggingManager(DepsGraph graph) {
        this.graph = graph;
    }

    /**
     * Register all tagging entries from a single {@link TaggingConfigCallback}.
     * For each {@link NodeTaggingCreator.TaggingEntry} in the config, and for each watched
     * {@link IData}, a programmatic subscriber is created on the respective {@link EventBus}.
     *
     * @param callback the callback binding a node to its tagging config
     * @param <T>      the node type
     */
    @SuppressWarnings("unchecked")
    public <T extends Node> void register(TaggingConfigCallback<T> callback) {
        T node = callback.node();
        NodeTaggingCreator<T> config = callback.config();

        for (NodeTaggingCreator.TaggingEntry<T> entry : config.getEntries()) {
            for (IData data : entry.watchedData()) {
                EventBus bus = this.resolveEventBus(data);

                // The consumer that will be invoked by the event bus on matching events.
                // Captures: graph, entry.listener, data (source), node.
                Consumer<ChangeEvent> consumer = event -> entry.listener().onEvent(this.graph, event, data, node);

                // Convert SubscribeEvent.Type[] to IEventConstraint<EventOrigin>[] for the bus API
                SubscribeEvent.Type[] constraintTypes = entry.constraints();

                @SuppressWarnings("rawtypes")
                Class eventType = entry.eventType();

                AbstractEventBus.Subscription subscription = bus.subscribe(
                        consumer, eventType, constraintTypes);

                this.subscriptions.add(new SubscriptionRecord(bus, subscription));
            }
        }
    }

    /**
     * Convenience: register multiple callbacks at once.
     */
    public void registerAll(Collection<TaggingConfigCallback<?>> callbacks) {
        for (TaggingConfigCallback<?> callback : callbacks) {
            this.registerUntyped(callback);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerUntyped(TaggingConfigCallback<?> callback) {
        this.register((TaggingConfigCallback) callback);
    }

    /**
     * Unsubscribe all listeners that were registered through this manager.
     * Should be called before a graph rebuild to prevent stale references.
     */
    public void unsubscribeAll() {
        for (SubscriptionRecord record : this.subscriptions) {
            record.bus.unsubscribe(record.subscription);
        }
        this.subscriptions.clear();
        this.eventBusCache.clear();
    }

    private EventBus resolveEventBus(IData data) {
        return this.eventBusCache.computeIfAbsent(data, IData::getEventBus);
    }

    // ---- Supporting types ----

    /**
     * Binds a concrete node instance to its {@link NodeTaggingCreator}.
     * Analogous to {@link com.minechinima.depsgraph.builder.ComponentBuilder.RelationBuilderCallback}.
     *
     * @param <T> the node type
     */
    public record TaggingConfigCallback<T extends Node>(T node, NodeTaggingCreator<T> config) {}

    /**
     * Internal record tracking an individual subscription so it can be removed later.
     */
    private record SubscriptionRecord(EventBus bus, AbstractEventBus.Subscription subscription) {}
}

