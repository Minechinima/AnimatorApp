package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.DataScope;
import com.minechinima.depsgraph.Node;
import com.minechinima.model.api.IData;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Configuration that describes <em>how</em> a dependency-graph {@link Node} should be tagged
 * when data changes.
 * One config holds listeners and functions that can tag any data with the context of the node of this config.
 * <br><br>
 * Three factory methods correspond to the three granularity levels:
 * <ol>
 *   <li>{@link #forDataScope} – just a {@link DataScope} and a tag</li>
 *   <li>{@link #forEventConstraints} – adds event-type and constraint filters</li>
 *   <li>{@link #custom} – fully custom listener</li>
 * </ol>
 * Levels 1 and 2 delegate to level 3 internally (DRY).
 *
 * @param <T> the type of dependency-graph node this config applies to
 */
public class NodeTaggingCreator<T extends Node> {

    private final List<TaggingEntry<T>> entries;

    private NodeTaggingCreator(List<TaggingEntry<T>> entries) {
        this.entries = Collections.unmodifiableList(entries);
    }

    public List<TaggingEntry<T>> getEntries() {
        return this.entries;
    }

    // ---- Level 1: DataScope + Tag ----

    /**
     * Listen for <em>all</em> {@link ChangeEvent}s on a {@link IData}.
     * When any fires the node is tagged with {@code tag}.
     *
     * @param data the data to watch
     * @param tag   the tag to apply
     * @param <T>   node type
     * @return a config with one entry
     */
    public static <T extends Node> NodeTaggingCreator<T> forData(IData data, INodeTag tag) {
        return forEventConstraints(new DataScope(data), ChangeEvent.class, null, tag);
    }

    /**
     * Listen for <em>all</em> {@link ChangeEvent}s on every {@link IData} in the given scope.
     * When any fires the node is tagged with {@code tag}.
     *
     * @param scope the data scope whose data instances to watch
     * @param tag   the tag to apply
     * @param <T>   node type
     * @return a config with one entry
     */
    public static <T extends Node> NodeTaggingCreator<T> forDataScope(DataScope scope, INodeTag tag) {
        return forEventConstraints(scope, ChangeEvent.class, null, tag);
    }

    // ---- Level 2: DataScope + event type + constraint + Tag ----

    /**
     * Listen for a specific {@link ChangeEvent} subtype on every {@link IData} in the given scope,
     * filtered by an optional {@link SubscribeEvent.Type} constraint.
     *
     * @param scope      the data scope whose data instances to watch
     * @param eventType  the concrete change-event class to filter for
     * @param constraint the event-origin constraint (nullable – {@code null} means ALL)
     * @param tag        the tag to apply
     * @param <T>        node type
     * @return a config with one entry
     */
    public static <T extends Node> NodeTaggingCreator<T> forEventConstraints(
            DataScope scope,
            Class<? extends ChangeEvent> eventType,
            @Nullable SubscribeEvent.Type constraint,
            INodeTag tag) {

        SubscribeEvent.Type[] constraints = constraint != null
                ? new SubscribeEvent.Type[]{constraint}
                : new SubscribeEvent.Type[]{SubscribeEvent.Type.ALL};

        NodeTaggingListener<T> defaultListener = (graph, event, source, node) -> graph.tag(node, tag);
        return custom(defaultListener, eventType, constraints, scope.getAllData());
    }

    public static <T extends Node> NodeTaggingCreator<T> forEventConstraints(
            IData scope,
            Class<? extends ChangeEvent> eventType,
            @Nullable SubscribeEvent.Type constraint,
            INodeTag tag) {
        return forEventConstraints(new DataScope(scope), eventType, constraint, tag);
    }

    // ---- Level 3: fully custom ----

    /**
     * Fully custom tagging functionality.
     *
     * @param listener    the listener to invoke
     * @param eventType   the change-event class to filter for (nullable – {@code null} means all ChangeEvents)
     * @param constraints the event-origin constraints to register with
     * @param watchedData the {@link IData} instances whose event buses will be subscribed to
     * @param <T>         node type
     * @return a config with one entry
     */
    public static <T extends Node> NodeTaggingCreator<T> custom(
            NodeTaggingListener<T> listener,
            @Nullable Class<? extends ChangeEvent> eventType,
            SubscribeEvent.Type[] constraints,
            Set<IData> watchedData) {

        Class<? extends ChangeEvent> effectiveEventType = eventType != null ? eventType : ChangeEvent.class;

        TaggingEntry<T> entry = new TaggingEntry<>(listener, effectiveEventType, constraints, watchedData);
        return new NodeTaggingCreator<>(List.of(entry));
    }

    /**
     * Convenience overload accepting varargs data instead of a set.
     */
    public static <T extends Node> NodeTaggingCreator<T> custom(
            NodeTaggingListener<T> listener,
            @Nullable Class<? extends ChangeEvent> eventType,
            SubscribeEvent.Type[] constraints,
            IData... watchedData) {

        Set<IData> dataSet = Collections.newSetFromMap(new IdentityHashMap<>());
        dataSet.addAll(Arrays.asList(watchedData));
        return custom(listener, eventType, constraints, dataSet);
    }

    /**
     * Merge multiple configs into one.
     */
    @SafeVarargs
    public static <T extends Node> NodeTaggingCreator<T> merge(NodeTaggingCreator<T>... configs) {
        List<TaggingEntry<T>> merged = new ArrayList<>();
        for (NodeTaggingCreator<T> config : configs) {
            merged.addAll(config.entries);
        }
        return new NodeTaggingCreator<>(merged);
    }

    // ---- Entry record ----

    /**
     * One atomic tagging registration: listener + filters + watched data.
     *
     * @param <T> the node type
     */
    public record TaggingEntry<T extends Node>(
            NodeTaggingListener<T> listener,
            Class<? extends ChangeEvent> eventType,
            SubscribeEvent.Type[] constraints,
            Set<IData> watchedData
    ) {}
}

