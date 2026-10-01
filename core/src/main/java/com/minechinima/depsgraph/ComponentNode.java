package com.minechinima.depsgraph;

import com.minechinima.model.api.IData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ComponentNode<DATA extends T, EVAL extends T, T extends IData> extends Node {
    private final DataNode<DATA, EVAL, T> owner;

    private OperationNode<EVAL> entry;
    private OperationNode<EVAL> exit;

    private final ComponentType type;
    private final Map<OperationKey, OperationNode<EVAL>> operations = new HashMap<>();
    /**
     * Scope which data is managed by this. Used for targeting nodes based on data attributes.
     * This is the scope only for the component, it can be different from the operation nodes, they need to be merged
     * later. Cannot merge here because Operation Nodes could still be added.
     * @implNote final to ensure data has 1-1 mapping with nodes.
     */
    @Nullable
    private final DataScope scopeOfComponent;

    public ComponentNode(DataNode<DATA, EVAL, T> owner, ComponentType key, @Nullable DataScope scope) {
        this.type = key;
        this.owner = owner;
        this.scopeOfComponent = scope;
    }

    public ComponentNode(DataNode<DATA, EVAL, T> owner, ComponentType key) {
        this(owner, key, null);
    }

    public DataNode<DATA, EVAL, T> getOwner() {
        return owner;
    }

    public @NotNull OperationNode<EVAL> getEntry() {
        return entry;
    }

    public @NotNull OperationNode<EVAL> getExit() {
        return exit;
    }

    public ComponentType getType() {
        return type;
    }

    public @Nullable DataScope getScope() {
        return this.scopeOfComponent;
    }

    /**
     * @return all operation nodes managed by this component node, including entry and exit. HashMap is a copy.
     */
    public Map<OperationKey, OperationNode<EVAL>> getOperations() {
        return new HashMap<>(this.operations);
    }

    /**
     * Merges the scopes of the component and all operation nodes and returns the merged scope.
     * @return the scope of the component and all operation nodes.
     */
    public DataScope getMergedScope() {
        DataScope mergedScope = this.scopeOfComponent;

        for (OperationNode<EVAL> node : this.operations.values()) {
            DataScope nodeScope = node.getScope();
            if (nodeScope != null) {
                if (mergedScope == null) {
                    mergedScope = nodeScope;
                } else {
                    mergedScope = mergedScope.merge(nodeScope);
                }
            }
        }

        return mergedScope;
    }

    /**
     * Set the entry operation.
     * @param entry
     * @throw RuntimeException if the entry operation is already set or if the entry operation is the same instance as the exit operation.
     */
    public void setEntry(@NotNull OperationNode<EVAL> entry) {
        Objects.requireNonNull(entry);
        /* changing the entry operation might cause unwanted relations/behaviour when other nodes are already connected to this entry */
        if (this.entry != null) throw new UnsupportedOperationException("Cannot set entry operation if it is already present.");
        if (entry == this.exit) throw new IllegalArgumentException("Entry and exit operation cannot be the same instance.");
        if (!this.isOperationNodePresent(entry)) {
            this.addOperationNode(entry);
        }

        this.entry = entry;
    }

    public void setExit(@NotNull OperationNode<EVAL> exit) {
        Objects.requireNonNull(exit);
        /* changing the exit operation might cause unwanted relations/behaviour when other nodes are already connected to this entry */
        if (this.exit != null) throw new UnsupportedOperationException("Cannot set entry operation if it is already present.");
        if (exit == this.entry) throw new IllegalArgumentException("Entry and exit operation cannot be the same instance.");
        if (!this.isOperationNodePresent(exit)) {
            this.addOperationNode(exit);
        }

        this.exit = exit;
    }

    /**
     * @param operation
     * @return true if the operation node instance is already registered.
     */
    public boolean isOperationNodePresent(OperationNode operation) {
        return this.operations.containsValue(operation);
    }

    public OperationNode<EVAL> findOperation(OperationNode.OperationCode key, String name) {
        return this.operations.get(new OperationKey(key, name));
    }

    public OperationNode<EVAL> addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                            OperationNode.OperationCode code) {
        return this.addOperation(func, code, null, null);
    }

    public OperationNode<EVAL> addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                            OperationNode.OperationCode code,
                                            @Nullable final String name) {
        return this.addOperation(func, code, name, null);
    }

    public OperationNode<EVAL> addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                            OperationNode.OperationCode code,
                                            @Nullable DataScope scope) {
        return this.addOperation(func, code, null, scope);
    }

    /**
     *
     * @param func
     * @param code
     * @param name can be null
     * @param scope datastructures that are managed by the operation node. Used for animation targetting.
     * @return
     */
    public OperationNode<EVAL> addOperation(OperationNode.EvalFunction<? super EVAL> func,
                                            OperationNode.OperationCode code,
                                            @Nullable final String name,
                                            @Nullable DataScope scope) {
        if (this.findOperation(code, name) != null) {
            throw new IllegalArgumentException("Operation with code " + code.getName() + " and name " + name + " already exists in component " + this.type.getName());
        }

        OperationNode<EVAL> node = new OperationNode<>(this, func, code, name, scope);
        this.addOperationNode(node);
        return node;
    }

    private void addOperationNode(OperationNode<EVAL> node) {
        if (node.getComponent() != this) throw new IllegalArgumentException("Owner component of operation node does not this.");

        /* check if the datascope intersects with another operation node */
        DataScope nodeScope;
        if ((nodeScope = node.getScope()) != null) {
            for (OperationNode<EVAL> otherNode : this.operations.values()) {
                DataScope otherNodeScope = otherNode.getScope();
                if (otherNodeScope != null && otherNodeScope.intersects(nodeScope)) {
                    throw new IllegalArgumentException("OperationNode data scope intersects with scope of already present operation node "
                            + otherNode.getCode().getName() + " in component " + this.type.getName());
                }
            }
        }

        this.operations.put(new OperationKey(node.getCode(), node.getName()), node);
    }

    /**
     * For identifying a Component and looking it up in a hashmap.
     * <br><br>
     * Advised to implement it in an enum.
     */
    public interface ComponentType {
        String getName();
    }

    /**
     * Used to identify an operation node in the component with additional information,
     * e.g. the name of the modifier for a modifier operation.<br><br>
     * This allows the same operation code to be used for multiple operation nodes in the same component.
     */
    public static class OperationKey {
        private final OperationNode.OperationCode code;
        private final String name;

        public OperationKey(OperationNode.OperationCode code, String name) {
            this.code = code;
            this.name = name;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof OperationKey that)) return false;
            return Objects.equals(this.toString(), that.toString());
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.toString());
        }

        @Override
        public String toString() {
            return "OperationKey{code=" + code.getName() +
                    ", name='" + name + '\'' +
                    '}';
        }
    }
}
