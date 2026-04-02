package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.ComponentNode;
import com.minechinima.depsgraph.DataScope;
import com.minechinima.depsgraph.OperationNode;
import com.minechinima.depsgraph.tagging.NodeTagging;
import com.minechinima.depsgraph.tagging.NodeTaggingManager;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Utility class for building operation nodes without needing to access the underlying internal API
 * of the dependency graph node classes.
 * Might be useful if we want to wrap exceptions of the internal api and log them instead of crashing.
 */
public class OperationNodesBuilder<DATA extends T, EVAL extends T, T extends IData> {
    private final Set<BufferBuilderCallback<OperationNode<EVAL>>> relationBuilderBuffer = new HashSet<>();
    private final Set<NodeTaggingManager.NodeTaggingCallback<OperationNode<EVAL>>> taggingConfigBuffer = new HashSet<>();
    private final ComponentNode<DATA, EVAL, T> componentNode;

    private Stack<OperationNode<EVAL>> consecutiveChain = new Stack<>();

    public OperationNodesBuilder(ComponentNode<DATA, EVAL, T> componentNode) {
        if (componentNode.getExit() == null || componentNode.getEntry() == null) {
            throw new IllegalArgumentException("Component node must have entry and exit operations before creating OperationNodesAssembler.");
        }

        this.componentNode = componentNode;
        this.consecutiveChain.add(componentNode.getEntry());
    }

    public Set<NodeTaggingManager.NodeTaggingCallback<OperationNode<EVAL>>> getTaggingConfigBuffer() {
        return new HashSet<>(this.taggingConfigBuffer);
    }

    public Set<BufferBuilderCallback<OperationNode<EVAL>>> getRelationBuilderBuffer() {
        return new HashSet<>(this.relationBuilderBuffer);
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
    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code,
                                              @Nullable final String name,
                                              @Nullable DataScope scope,
                                              @Nullable BufferedBuilderFunc<OperationNode<EVAL>> operationNodeRelationBuilder) {
        return this.addOperation(func, code, name, scope, operationNodeRelationBuilder, null);
    }

    /**
     * Full-signature method for adding an operation node with all options including tagging.
     */
    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code,
                                              @Nullable final String name,
                                              @Nullable DataScope scope,
                                              @Nullable BufferedBuilderFunc<OperationNode<EVAL>> operationNodeRelationBuilder,
                                              @Nullable NodeTagging<OperationNode<EVAL>> taggingConfig) {
        OperationNode<EVAL> node = componentNode.addOperation(func, code, name, scope);
        if (operationNodeRelationBuilder != null) {
            this.relationBuilderBuffer.add(new BufferBuilderCallback<>(node, operationNodeRelationBuilder));
        }
        if (taggingConfig != null) {
            this.taggingConfigBuffer.add(new NodeTaggingManager.NodeTaggingCallback<>(node, taggingConfig));
        }

        return new RelationBuilderHelper(node);
    }

    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code,
                                              @Nullable final String name,
                                              @Nullable BufferedBuilderFunc<OperationNode<EVAL>> operationNodeRelationBuilder) {
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
                                              @Nullable BufferedBuilderFunc<OperationNode<EVAL>> operationNodeRelationBuilder) {
        return this.addOperation(func, code, null, scope, operationNodeRelationBuilder, null);
    }

    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code) {
        return this.addOperation(func, code, null, null, null, null);
    }

    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code,
                                              @Nullable BufferedBuilderFunc<OperationNode<EVAL>> operationNodeRelationBuilder) {
        return this.addOperation(func, code, null, null, operationNodeRelationBuilder, null);
    }

    /**
     * Add an operation with a tagging config but no relation builder.
     */
    public RelationBuilderHelper addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                              OperationNode.OperationCode code,
                                              @Nullable final String name,
                                              @Nullable DataScope scope,
                                              @Nullable NodeTagging<OperationNode<EVAL>> taggingConfig) {
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
            OperationNode<EVAL> last = OperationNodesBuilder.this.consecutiveChain.peek();
            this.addToChain(null);
            return this;
        }

        /**
         * Creates a relation from the last created operation node to this
         * newly created node.
         * Only adds to consecutive chain, meaning nodes that were not added to the chain are ignored.
         *
         * @param name custom name for the relation.
         */
        public RelationBuilderHelper addToChain(String name) {
            Stack<OperationNode<EVAL>> chain = OperationNodesBuilder.this.consecutiveChain;
            OperationNode<EVAL> last = chain.peek();

            if (name == null) {
                last.addFromRelationTo(this.added);
            } else {
                last.addFromRelationTo(this.added, name);
            }

            OperationNodesBuilder.this.consecutiveChain.push(this.added);
            return this;
        }

        public RelationBuilderHelper relationFromEntryToThis(String name) {
            OperationNodesBuilder.this.getEntry().addFromRelationTo(this.added, name);
            return this;
        }

        public RelationBuilderHelper relationFromEntryToThis() {
            OperationNodesBuilder.this.getEntry().addFromRelationTo(this.added);
            return this;
        }

        public RelationBuilderHelper relationFromThisToExit() {
            this.added.addFromRelationTo(OperationNodesBuilder.this.getExit());

            return this;
        }

        public RelationBuilderHelper relationFromThisToExit(String name) {
            this.added.addFromRelationTo(OperationNodesBuilder.this.getExit(), name);

            return this;
        }

        public RelationBuilderHelper relationTo(OperationNode<EVAL> to) {
            this.added.addFromRelationTo(to);
            return this;
        }

        public RelationBuilderHelper relationTo(OperationNode<EVAL> to, String name) {
            this.added.addFromRelationTo(to, name);
            return this;
        }
    }
}
