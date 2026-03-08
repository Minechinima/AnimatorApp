package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.*;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.Nullable;

import java.util.*;

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

    public BuildResult<T> buildComponent(final DataNode<T> owner) {
        final T dataBase = owner.getOriginalData();
        ComponentNode<T> component = new ComponentNode<>(owner, this.key, this.getDataScope(owner.getOriginalData()));
        this.addEntryExitOperation(component);
        if (component.getEntry() == null || component.getExit() == null) {
            throw new IllegalStateException("Entry and exit operation must be set in addEntryExitOperation.");
        }

        Set<OpNodeRelationBuilder<?, T>> operationRelationBuilders = new HashSet<>();

        for (OperationNodesBuilder<T> builder : this.operationNodesBuilders) {
            /*
             * Create a helper for every builder separately. This allows to create separate consecutive chains for every builder,
             * which could improve parallelity and multithreading.
             * Atm no use case for further flexibility with building operation nodes to create relations between two separate builders.
             */
            OperationNodesBuilderHelper helper = new OperationNodesBuilderHelper(component);
            builder.addOperationNodes(dataBase, helper);
            operationRelationBuilders.addAll(helper.relationBuilderBuffer);
        }

        return new BuildResult<>(component, operationRelationBuilders);
    }

    /**
     * @return The attributes that belong to this component, e.g. for a transform component it would be the location, rotation and scale attributes.
     */
    @Nullable
    public abstract DataScope getDataScope(T data);

    public abstract void addEntryExitOperation(ComponentNode<T> component);

    /**
     * Function to add the operation nodes to the component.
     * Called after entry and exit have been added, so internal operations can be built with relations to them.
     * @param <T>
     */
    @FunctionalInterface
    public interface OperationNodesBuilder<T extends BaseStructure> {
        /**
         * Is called after entry and exit have been built so internal operations can be built with relations to them.
         * Adds operation nodes to the component.
         * <br><br>
         * Internal relations can be built here, e.g. from entry to the first internal operation, between internal operations and from the last internal operation to the exit.
         * For relations to any other nodes, even nodes in the same data node, the external {@link RelationBuilder} should be used,
         * because the other nodes might not be built yet when this method is called.
         * @param data the original database value
         */
        @Nullable
        void addOperationNodes(T data, ComponentBuilder<T>.OperationNodesBuilderHelper helper);
    }

    /**
     * Utility data class to store operation node and its relation builder callback.
     */
    public record OpNodeRelationBuilder<OP extends OperationNode<T>, T extends BaseStructure>(OP operationNode,
                                                                                              RelationBuilder<OP> relationBuilder) {

    }

    /**
     * Return type of the build method.
     */
    public record BuildResult<T extends BaseStructure>(ComponentNode<T> component,
                                                       Set<OpNodeRelationBuilder<?, T>> operationRelationBuilders) {

    }

    /**
     * Utility class for building operation nodes without needing to access the underlying internal API
     * of the dependency graph node classes.
     * Might be useful if we want to wrap exceptions of the internal api and log them instead of crashing.
     */
    public class OperationNodesBuilderHelper {
        private final Set<OpNodeRelationBuilder<?, T>> relationBuilderBuffer = new HashSet<>();
        private final ComponentNode<T> componentNode;

        private Stack<OperationNode<T>> consecutiveChain = new Stack<>();

        public OperationNodesBuilderHelper(ComponentNode<T> componentNode) {
            if (componentNode.getExit() == null || componentNode.getEntry() == null) {
                throw new IllegalArgumentException("Component node must have entry and exit operations before creating OperationNodesAssembler.");
            }

            this.componentNode = componentNode;

            consecutiveChain.add(componentNode.getEntry());
        }

        public OperationNode<T> getExit() {
            return componentNode.getExit();
        }

        public OperationNode<T> getEntry() {
            return componentNode.getEntry();
        }

        /*
         * Methods for adding an operation node to the component
         */

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<T>> operationNodeRelationBuilder) {
            OperationNode<T> node = componentNode.addOperation(func, code, name, scope);
            if (operationNodeRelationBuilder != null) {
                this.relationBuilderBuffer.add(new OpNodeRelationBuilder<OperationNode<T>, T>(node, operationNodeRelationBuilder));
            }

            return new RelationBuilderHelper(node);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope) {
            return this.addOperation(func, code, name, scope, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable DataScope scope) {
            return this.addOperation(func, code, null, scope, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<T>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, null, scope, operationNodeRelationBuilder);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code) {
            return this.addOperation(func, code, null, null, null);
        }

        public RelationBuilderHelper addOperation(OperationNode.EvalFunction<T, OperationNodeDTO<T>> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable RelationBuilder<OperationNode<T>> operationNodeRelationBuilder) {
            return this.addOperation(func, code, null, null, operationNodeRelationBuilder);
        }

        /*
         * Methods for adding an operation node with a custom DTO factory
         */

        public <D extends OperationNodeDTO<T>> RelationBuilderHelper addOperation(OperationNode.DTOFactory<T, D> factory,
                                                  OperationNode.EvalFunction<T, D> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope,
                                                  @Nullable RelationBuilder<OperationNode<T>> operationNodeRelationBuilder) {
            OperationNode<T> node = componentNode.addOperation(factory, func, code, name, scope);
            if (operationNodeRelationBuilder != null) {
                this.relationBuilderBuffer.add(new OpNodeRelationBuilder<OperationNode<T>, T>(node, operationNodeRelationBuilder));
            }

            return new RelationBuilderHelper(node);
        }

        public <D extends OperationNodeDTO<T>> RelationBuilderHelper addOperation(OperationNode.DTOFactory<T, D> factory,
                                                  OperationNode.EvalFunction<T, D> func,
                                                  OperationNode.OperationCode code) {
            return this.addOperation(factory, func, code, null, null, null);
        }

        public <D extends OperationNodeDTO<T>> RelationBuilderHelper addOperation(OperationNode.DTOFactory<T, D> factory,
                                                  OperationNode.EvalFunction<T, D> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable final String name,
                                                  @Nullable DataScope scope) {
            return this.addOperation(factory, func, code, name, scope, null);
        }

        public <D extends OperationNodeDTO<T>> RelationBuilderHelper addOperation(OperationNode.DTOFactory<T, D> factory,
                                                  OperationNode.EvalFunction<T, D> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable DataScope scope) {
            return this.addOperation(factory, func, code, null, scope, null);
        }

        public <D extends OperationNodeDTO<T>> RelationBuilderHelper addOperation(OperationNode.DTOFactory<T, D> factory,
                                                  OperationNode.EvalFunction<T, D> func,
                                                  OperationNode.OperationCode code,
                                                  @Nullable RelationBuilder<OperationNode<T>> operationNodeRelationBuilder) {
            return this.addOperation(factory, func, code, null, null, operationNodeRelationBuilder);
        }

        /**
         * Helper for creating relations. Wraps the internal API for easier access
         * and error handling perhaps.
         */
        public class RelationBuilderHelper {
            private final OperationNode<T> added;

            protected RelationBuilderHelper(OperationNode<T> added) {
                this.added = added;
            }

            public OperationNode<T> get() {
                return added;
            }

            /**
             * Creates a relation from the last created operation node to this
             * newly created node.
             * Only adds to consecutive chain, meaning nodes that were not added to the chain are ignored.
             */
            public RelationBuilderHelper addToChain() {
                OperationNode<T> last = OperationNodesBuilderHelper.this.consecutiveChain.peek();
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
                Stack<OperationNode<T>> chain = OperationNodesBuilderHelper.this.consecutiveChain;
                OperationNode<T> last = chain.peek();

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
                this.relationFromThisToExit( this.added.getIdentifier() + " -> " + OperationNodesBuilderHelper.this.getExit().getIdentifier());
                return this;
            }

            public RelationBuilderHelper relationFromThisToExit(String name) {
                this.added.addFromRelationTo(OperationNodesBuilderHelper.this.getExit(), name);

                return this;
            }

            public RelationBuilderHelper relationTo(OperationNode<T> to) {
                this.added.addFromRelationTo(to, this.added.getIdentifier() + " -> " + to.getIdentifier());
                return this;
            }

            public RelationBuilderHelper relationTo(OperationNode<T> to, String name) {
                this.added.addFromRelationTo(to, name);
                return this;
            }
        }
    }
}
