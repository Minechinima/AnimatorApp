package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.*;
import com.minechinima.depsgraph.tagging.NodeTagging;
import com.minechinima.depsgraph.tagging.NodeTaggingManager.NodeTaggingCallback;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Factory for building one data component and its operations.
 * Only internal operation relations should be built here, e.g. the entry and exit operations and the relation between them.
 * <br><br>
 * Relations to other nodes should be built in {@link BufferedBuilderFunc}. Important when the dependeny graph is not finished
 * yet and the other nodes are not available. Therefore this builder gets no access to dependency graph.
 * @param <DATA> type of the database value
 * @param <EVAL> type of the copy of the original data, which is used for evaluation and update operations.
 */
public abstract class ComponentBuilder<DATA extends T, EVAL extends T, T extends IData> {
    /**
     * The key of the component. Stored here as final to enforce
     * singletons for keys, so that we can safely use the key for hashmap lookups in the component nodes and avoid unnecessary
     * instances.
     */
    private final ComponentNode.ComponentType key;
    /**
     * Builders for the internal operation nodes, except entry and exit, which are built separately.
     */
    private final List<OperationNodesBuildFunc<DATA, EVAL, T>> operationNodesBuilders = new LinkedList<>();
    /**
     * A set of relation builders for the entire component.
     */
    private final List<BufferedBuilderFunc<ComponentNode<DATA, EVAL, T>>> relationBuilders = new LinkedList<>();
    /**
     * Tagging configurations for the component node itself.
     */
    private final List<ComponentTaggingCallback<DATA, EVAL, T>> componentTaggingConfigs = new LinkedList<>();

    public ComponentBuilder(ComponentNode.ComponentType key) {
        this.key = key;
    }

    public final ComponentNode.ComponentType getType() {
        return key;
    }

    public void addOperationNodeBuilder(OperationNodesBuildFunc<DATA, EVAL, T> builder) {
        if (this.operationNodesBuilders.contains(builder)) return;
        this.operationNodesBuilders.add(builder);
    }

    public void addComponentRelationBuilder(BufferedBuilderFunc<ComponentNode<DATA, EVAL, T>> builder) {
        if (this.relationBuilders.contains(builder)) return;
        this.relationBuilders.add(builder);
    }

    /**
     * Add a tagging configuration for the component node itself.
     * The tagging will be registered after the graph is fully built.
     */
    public void addComponentTaggingConfig(ComponentTaggingCallback<DATA, EVAL, T> config) {
        this.componentTaggingConfigs.add(config);
    }

    public BuildResult<DATA, EVAL, T> buildComponent(final DataNode<DATA, EVAL, T> owner) {
        final DATA dataBase = owner.getOriginalData();
        ComponentNode<DATA, EVAL, T> component = new ComponentNode<>(owner, this.key, this.getDataScope(dataBase));
        this.addEntryExitOperation(component);
        if (component.getEntry() == null || component.getExit() == null) {
            throw new IllegalStateException("Entry and exit operation must be set in addEntryExitOperation.");
        }

        Set<BufferBuilderCallback<OperationNode<EVAL>>> operationRelationBuilders = new HashSet<>();
        Set<BufferBuilderCallback<ComponentNode<DATA, EVAL, T>>> relationBuilders = new HashSet<>();
        Set<NodeTaggingCallback<?>> nodeTaggingCallbacks = new HashSet<>();

        for (OperationNodesBuildFunc<DATA, EVAL, T> builder : this.operationNodesBuilders) {
            /*
             * Create a helper for every builder separately. This allows to create separate consecutive chains for every builder,
             * which could improve parallelity and multithreading.
             * Atm no use case for further flexibility with building operation nodes to create relations between two separate builders.
             */
            OperationNodesBuilder<DATA, EVAL, T> helper = new OperationNodesBuilder<>(component);
            builder.addOperationNodes(dataBase, helper);
            operationRelationBuilders.addAll(helper.getRelationBuilderBuffer());
            nodeTaggingCallbacks.addAll(helper.getTaggingConfigBuffer());
        }

        for (BufferedBuilderFunc<ComponentNode<DATA, EVAL, T>> relationBuilder : this.relationBuilders) {
            relationBuilders.add(new BufferBuilderCallback<>(component, relationBuilder));
        }

        /* component-level tagging configs */
        for (ComponentTaggingCallback<DATA, EVAL, T> callback : this.componentTaggingConfigs) {
            nodeTaggingCallbacks.add(new NodeTaggingCallback<>(component, callback.createTagging(dataBase)));
        }

        return new BuildResult<>(component, operationRelationBuilders, relationBuilders, nodeTaggingCallbacks);
    }

    /**
     * @return The attributes that belong to this component, e.g. for a transform component it would be the location, rotation and scale attributes.
     */
    @Nullable
    public abstract DataScope getDataScope(DATA data);

    public abstract void addEntryExitOperation(ComponentNode<DATA, EVAL, T> component);

    /**
     * Return type of the build method.
     */
    public record BuildResult<DATA extends T, EVAL extends T, T extends IData>
            (ComponentNode<DATA, EVAL, T> component,
             Set<BufferBuilderCallback<OperationNode<EVAL>>> operationRelationBuilders,
             Set<BufferBuilderCallback<ComponentNode<DATA, EVAL, T>>> componentRelationBuilders,
             Set<NodeTaggingCallback<?>> nodeTaggingCallbacks) {

    }

    /**
     * Callback to create the tagging listeners for the component node.
     * Necessary to accept the database value as parameter.
     * @param <DATA>
     * @param <EVAL>
     * @param <T>
     */
    @FunctionalInterface
    public interface ComponentTaggingCallback<DATA extends T, EVAL extends T, T extends IData> {
        NodeTagging<ComponentNode<DATA, EVAL, T>> createTagging(DATA data);
    }
}
