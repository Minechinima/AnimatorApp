package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.ComponentNode;
import com.minechinima.depsgraph.DataNode;
import com.minechinima.depsgraph.DataScope;
import com.minechinima.depsgraph.OperationNode;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Factory for building one data component and its operations.
 * Only internal operation relations should be built here, e.g. the entry and exit operations and the relation between them.
 * <br><br>
 * Relations to other nodes should be built in {@link RelationBuilder}. Important when the dependeny graph is not finished
 * yet and the other nodes are not available. Therefore this builder gets no access to dependency graph.
 * @param <T>
 */
public abstract class ComponentBuilder<T extends BaseStructure> {
    /**
     * The key of the component. Stored here as final to enforce
     * singletons for keys, so that we can safely use the key for hashmap lookups in the component nodes and avoid unnecessary
     * instances.
     */
    private final ComponentNode.ComponentType key;
    /**
     * Builders for the internal operation nodes, except entry and exit, which are built separately.
     */
    private final List<OperationNodesBuilder<T>> operationNodesBuilders = new LinkedList<>();
    /**
     * A set of relation builders for the entire component.
     */
    private final List<RelationBuilder<ComponentNode<T>>> relationBuilders = new LinkedList<>();

    public ComponentBuilder(ComponentNode.ComponentType key) {
        this.key = key;
    }

    public void addOperationNodeBuilder(OperationNodesBuilder<T> builder) {
        if (this.operationNodesBuilders.contains(builder)) return;
        this.operationNodesBuilders.add(builder);
    }

    public void addRelationBuilder(RelationBuilder<ComponentNode<T>> builder) {
        if (this.relationBuilders.contains(builder)) return;
        this.relationBuilders.add(builder);
    }

    public ComponentNode<T> buildComponent(DataNode<T> owner) {
        ComponentNode<T> component = new ComponentNode<>(owner, this.key, this.getDataScope(owner.getOriginalData()));
        this.addEntryExitOperation(component);
        if (component.getEntry() == null || component.getExit() == null) {
            throw new IllegalStateException("Entry and exit operation must be set in addEntryExitOperation.");
        }

        this.operationNodesBuilders.forEach(builder -> builder.addOperationNodes(component, component.getEntry(), component.getExit()));

        return component;
    }

    /**
     * @return The attributes that belong to this component, e.g. for a transform component it would be the location, rotation and scale attributes.
     */
    @Nullable
    public abstract DataScope getDataScope(T data);

    public abstract void addEntryExitOperation(ComponentNode<T> component);

    @FunctionalInterface
    public interface OperationNodesBuilder<T extends BaseStructure> {
        /**
         * Is called after entry and exit have been built so internal operations can be built with relations to them.
         * Adds operation nodes to the component.
         * <br><br>
         * Internal relations can be built here, e.g. from entry to the first internal operation, between internal operations and from the last internal operation to the exit.
         * For relations to any other nodes, even nodes in the same data node, the external {@link RelationBuilder} should be used,
         * because the other nodes might not be built yet when this method is called.
         * @param component
         * @param entry
         * @param exit
         * @return optional relation builders for specific operation nodes.
         */
        @Nullable
        Set<Pair<OperationNode<T>, RelationBuilder<OperationNode<T>>>> addOperationNodes(ComponentNode<T> component,
                                                                                         OperationNode<T> entry,
                                                                                         OperationNode<T> exit);
    }
}
