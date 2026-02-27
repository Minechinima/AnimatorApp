package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * One node represents one datastructure to update. A data node is further structured into
 * sub components for different aspects of the data, see {@link ComponentNode}.
 *
 * @param <T>
 */
public class DataNode<T extends BaseStructure> extends Node {
    private final T originalData;
    private T copy;
    private final Map<ComponentNode.ComponentType, ComponentNode<T>> components = new HashMap<>();
    /**
     * Allows a global entry component executed always at the beginning of all
     * other operations
     */
    private ComponentNode<T> entryComponent;
    /**
     * Allows a global exit component executed always at the end of all
     * other operations
     */
    private ComponentNode<T> exitComponent;

    public DataNode(T originalData) {
        this.originalData = originalData;
    }

    public T getOriginalData() {
        return originalData;
    }

    public T getCopy() {
        return copy;
    }

    @Nullable
    public ComponentNode<T> getComponent(ComponentNode.ComponentType type) {
        return this.components.get(type);
    }

    public Set<ComponentNode<T>> getComponents() {
        return Set.copyOf(this.components.values());
    }

    @Override
    public @NotNull OperationNode<T> getEntry() {
        return this.entryComponent.getEntry();
    }

    @Override
    public @NotNull OperationNode<T> getExit() {
        return this.exitComponent.getExit();
    }

    public void addComponent(ComponentNode<T> componentNode) {
        if (this.components.containsValue(componentNode) || this.components.containsKey(componentNode.getType())) {
            throw new IllegalArgumentException("Component already present in data node.");
        }

        if (componentNode.getOwner() != this) {
            throw new IllegalArgumentException("Component node does not belong to this data node.");
        }

        DataScope nodeScope;
        if ((nodeScope = componentNode.getMergedScope()) != null) {
            for (ComponentNode<T> otherNode : this.components.values()) {
                DataScope otherNodeScope = otherNode.getMergedScope();
                if (otherNodeScope != null && otherNodeScope.intersects(nodeScope)) {
                    throw new IllegalArgumentException("ComponentNode data scope intersects with scope of already present component node " + otherNode.getType());
                }
            }
        }

        this.components.put(componentNode.getType(), componentNode);
        this.getEntry().addFromRelationTo(componentNode, "DataNode Entry -> Component " + componentNode.getType());
        componentNode.addFromRelationTo(this.getExit(), "Component " + componentNode.getType() + " -> DataNode Exit");
    }

    public boolean isComponentNodePresent(ComponentNode node) {
        return this.components.containsValue(node);
    }

    public void setEntry(@NotNull ComponentNode<T> entry) {
        Objects.requireNonNull(entry);
        if (this.entryComponent != null) {
            throw new IllegalStateException("Entry component already set.");
        }

        if (entry == this.exitComponent) {
            throw new IllegalArgumentException("Entry and exit component cannot be the same instance.");
        }

        if (!this.isComponentNodePresent(entry)) {
            this.addComponent(entry);
        }

        this.entryComponent = entry;
    }

    public void setExit(@NotNull ComponentNode<T> exit) {
        Objects.requireNonNull(exit);
        if (this.exitComponent != null) {
            throw new IllegalStateException("Entry component already set.");
        }

        if (exit == this.entryComponent) {
            throw new IllegalArgumentException("Entry and exit component cannot be the same instance.");
        }

        if (!this.isComponentNodePresent(exit)) {
            this.addComponent(exit);
        }

        this.exitComponent = exit;
    }
}
