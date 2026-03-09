package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.*;
import com.minechinima.depsgraph.tagging.NodeTaggingCreator;
import com.minechinima.depsgraph.tagging.NodeTaggingManager.TaggingConfigCallback;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Factory for building one data component and its operations.
 * Only internal operation relations should be built here, e.g. the entry and exit operations and the relation between them.
 * <br><br>
 * Relations to other nodes should be built in {@link RelationBuilder}. Important when the dependeny graph is not finished
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
    private final List<OperationNodesBuilder<DATA, EVAL, T>> operationNodesBuilders = new LinkedList<>();
    /**
     * A set of relation builders for the entire component.
     */
    private final List<RelationBuilder<ComponentNode<DATA, EVAL, T>>> relationBuilders = new LinkedList<>();
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

    public void addOperationNodeBuilder(OperationNodesBuilder<DATA, EVAL, T> builder) {
        if (this.operationNodesBuilders.contains(builder)) return;
        this.operationNodesBuilders.add(builder);
    }

    public void addComponentRelationBuilder(RelationBuilder<ComponentNode<DATA, EVAL, T>> builder) {
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

        Set<RelationBuilderCallback<OperationNode<EVAL>>> operationRelationBuilders = new HashSet<>();
        Set<RelationBuilderCallback<ComponentNode<DATA, EVAL, T>>> relationBuilders = new HashSet<>();
        Set<TaggingConfigCallback<?>> taggingConfigCallbacks = new HashSet<>();

        for (OperationNodesBuilder<DATA, EVAL, T> builder : this.operationNodesBuilders) {
            /*
             * Create a helper for every builder separately. This allows to create separate consecutive chains for every builder,
             * which could improve parallelity and multithreading.
             * Atm no use case for further flexibility with building operation nodes to create relations between two separate builders.
             */
            OperationNodesBuilderHelper helper = new OperationNodesBuilderHelper(component);
            builder.addOperationNodes(dataBase, helper);
            operationRelationBuilders.addAll(helper.relationBuilderBuffer);
            taggingConfigCallbacks.addAll(helper.taggingConfigBuffer);
        }

        for (RelationBuilder<ComponentNode<DATA, EVAL, T>> relationBuilder : this.relationBuilders) {
            relationBuilders.add(new RelationBuilderCallback<>(component, relationBuilder));
        }

        /* component-level tagging configs */
        for (ComponentTaggingCallback<DATA, EVAL, T> callback : this.componentTaggingConfigs) {
            taggingConfigCallbacks.add(new TaggingConfigCallback<>(component, callback.createTagConfig(dataBase)));
        }

        return new BuildResult<>(component, operationRelationBuilders, relationBuilders, taggingConfigCallbacks);
    }

    /**
     * @return The attributes that belong to this component, e.g. for a transform component it would be the location, rotation and scale attributes.
     */
    @Nullable
    public abstract DataScope getDataScope(DATA data);

    public abstract void addEntryExitOperation(ComponentNode<DATA, EVAL, T> component);

    /**
     * Utility class to bind the node to the relation builder callback, so
     * that it does not need to be passed as parameter later on again.
     * @param <T>
     */
    public static class RelationBuilderCallback<T extends Node> {
        private final T node;
        private final RelationBuilder<T> relationBuilder;

        public RelationBuilderCallback(T node, RelationBuilder<T> relationBuilder) {
            this.node = node;
            this.relationBuilder = relationBuilder;
        }

        public void build(DepsGraph graph) {
            this.relationBuilder.buildRelations(graph, this.node);
        }
    }

    /**
     * Return type of the build method.
     */
    public record BuildResult<DATA extends T, EVAL extends T, T extends IData>
            (ComponentNode<DATA, EVAL, T> component,
             Set<RelationBuilderCallback<OperationNode<EVAL>>> operationRelationBuilders,
             Set<RelationBuilderCallback<ComponentNode<DATA, EVAL, T>>> componentRelationBuilders,
             Set<TaggingConfigCallback<?>> taggingConfigCallbacks) {

    }

    /**
     * Utility class for building operation nodes without needing to access the underlying internal API
     * of the dependency graph node classes.
     * Might be useful if we want to wrap exceptions of the internal api and log them instead of crashing.
     */
    //TODO extract from component builder
    public static class OperationNodesBuilderHelper<DATA extends T, EVAL extends T, T extends IData> {
        private final Set<RelationBuilderCallback<OperationNode<EVAL>>> relationBuilderBuffer = new HashSet<>();
        private final Set<TaggingConfigCallback<OperationNode<EVAL>>> taggingConfigBuffer = new HashSet<>();
        private final ComponentNode<DATA, EVAL, T> componentNode;

        private Stack<OperationNode<EVAL>> consecutiveChain = new Stack<>();

        public OperationNodesBuilderHelper(ComponentNode<DATA, EVAL, T> componentNode) {
            if (componentNode.getExit() == null || componentNode.getEntry() == null) {
                throw new IllegalArgumentException("Component node must have entry and exit operations before creating OperationNodesAssembler.");
            }

            this.componentNode = componentNode;
            this.consecutiveChain.add(componentNode.getEntry());
        }

        public ComponentNode<DATA, EVAL, T> getComponent() {
            return this.componentNode;
        }

        public OperationNode<EVAL> getExit() {
            return componentNode.getExit();
        }

        public OperationNode<EVAL> getEntry() {
            return componentNode.getEntry();
        }

        /*
         * Methods for adding an operation node to the component
         */
        //TODO add simpler methods for datascope that just take IData...

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<EVAL>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, name, scope, operationNodeRelationBuilder, null);
        }

        /**
         * Full-signature method for adding an operation node with all options including tagging.
         */
        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<EVAL>> operationNodeRelationBuilder,
                                                  @Nullable NodeTaggingCreator<OperationNode<EVAL>> taggingConfig) {
            OperationNode<EVAL> node = componentNode.addOperation(func, code, name, scope);
            if (operationNodeRelationBuilder != null) {
                this.relationBuilderBuffer.add(new RelationBuilderCallback<>(node, operationNodeRelationBuilder));
            }
            if (taggingConfig != null) {
                this.taggingConfigBuffer.add(new TaggingConfigCallback<>(node, taggingConfig));
            }

            return new RelationBuilderHelper(node);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable RelationBuilder<OperationNode<EVAL>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, name, null, operationNodeRelationBuilder, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope) {
            return this.addOperation(func, code, name, scope, null, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable DataScope scope) {
            return this.addOperation(func, code, null, scope, null, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<EVAL>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, null, scope, operationNodeRelationBuilder, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code) {
            return this.addOperation(func, code, null, null, null, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable RelationBuilder<OperationNode<EVAL>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, null, null, operationNodeRelationBuilder, null);
        }

        /**
         * Add an operation with a tagging config but no relation builder.
         */
        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope,
                                                  @Nullable NodeTaggingCreator<OperationNode<EVAL>> taggingConfig) {
            return this.addOperation(func, code, name, scope, null, taggingConfig);
        }

        /**
         * Build a relation from the last chained operation node to the exit operation
         * of the component, only if the last chained operation node is not the entry of the component.
         */
        public void chainToExit() {
            OperationNode<EVAL> last = this.consecutiveChain.peek();
            if (last == this.getEntry() || last == this.getExit()) {
                return;
            }
            RelationBuilderHelper helper = new RelationBuilderHelper(last);
            helper.relationFromThisToExit();
        }

        /**
         * Helper for creating relations for operation nodes.
         * Has utility functions to easily create a chain of operations.
         * Wraps the internal API for easier access and error handling perhaps.
         */
        public class RelationBuilderHelper {
            private final OperationNode<EVAL> added;

            protected RelationBuilderHelper(OperationNode<EVAL> added) {
                this.added = added;
            }

            public OperationNode<EVAL> get() {
                return added;
            }

            /**
             * Creates a relation from the last created operation node to this
             * newly created node.
             * Only adds to consecutive chain, meaning nodes that were not added to the chain are ignored.
             */
            public RelationBuilderHelper addToChain() {
                OperationNode<EVAL> last = OperationNodesBuilderHelper.this.consecutiveChain.peek();
                this.addToChain(last.getIdentifier() + " -> " + this.added.getIdentifier());
                return this;
            }

            /**
             * Creates a relation from the last created operation node to this
             * newly created node.
             * Only adds to consecutive chain, meaning nodes that were not added to the chain are ignored.
             * @param name custom name for the relation.
             */
            public RelationBuilderHelper addToChain(String name) {
                Stack<OperationNode<EVAL>> chain = OperationNodesBuilderHelper.this.consecutiveChain;
                OperationNode<EVAL> last = chain.peek();

                last.addFromRelationTo(this.added, name);
                OperationNodesBuilderHelper.this.consecutiveChain.push(this.added);
                return this;
            }

            public RelationBuilderHelper relationFromEntryToThis(String name) {
                OperationNodesBuilderHelper.this.getEntry().addFromRelationTo(this.added, name);
                return this;
            }

            public RelationBuilderHelper relationFromEntryToThis() {
                this.relationFromEntryToThis(OperationNodesBuilderHelper.this.getEntry().getIdentifier() + " -> " + this.added.getIdentifier());
                return this;
            }

            public RelationBuilderHelper relationFromThisToExit() {
                //TODO move name creation of relation to Node
                this.relationFromThisToExit( this.added.getIdentifier() + " -> " + OperationNodesBuilderHelper.this.getExit().getIdentifier());
                return this;
            }

            public RelationBuilderHelper relationFromThisToExit(String name) {
                this.added.addFromRelationTo(OperationNodesBuilderHelper.this.getExit(), name);

                return this;
            }

            public RelationBuilderHelper relationTo(OperationNode<EVAL> to) {
                this.added.addFromRelationTo(to, this.added.getIdentifier() + " -> " + to.getIdentifier());
                return this;
            }

            public RelationBuilderHelper relationTo(OperationNode<EVAL> to, String name) {
                this.added.addFromRelationTo(to, name);
                return this;
            }
        }
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
        NodeTaggingCreator<ComponentNode<DATA, EVAL, T>> createTagConfig(DATA data);
    }
}
