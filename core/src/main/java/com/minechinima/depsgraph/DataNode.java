package com.minechinima.depsgraph;

import com.minechinima.model.api.IData;
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
 * @param <DATA> type of the original database value this node handles.
 * @param <EVAL> type of the copy of the original data, which is used for evaluation and update operations.
 *                   This allows to have a different type for the copy, e.g. a wrapper around the original data with additional information.
 * @param <T> the super type of both EVAL and DATA so the UI can either use database or evaluated copy interchangeably.
 */
public class DataNode<DATA extends T, EVAL extends T, T extends IData> extends Node {
    private final DATA originalData;
    // TODO copy-on-write system
    //  the copy system should be another component (just like in blender) with relations to all other components of the node
    //  idea: copy operation node for every operation node so it can watch the data of the specific operation node.
    //  problem: copy operation node needs to tag itself for reevaluation when the data changes. Currently we only have 1-1 mapping of data to operation nodes,
    //  copy-operation node would require a 1-n mapping of data to nodes at least for update tagging.
    // TODO: more generic updating system: one where you can register custom event listeners or just provide data.
    private EVAL copy;
    private final Map<ComponentNode.ComponentType, ComponentNode<DATA, EVAL, T>> components = new HashMap<>();
    /**
     * Allows a global entry component executed always at the beginning of all
     * other operations
     */
    private ComponentNode<DATA, EVAL, T> entryComponent;
    /**
     * Allows a global exit component executed always at the end of all
     * other operations
     */
    private ComponentNode<DATA, EVAL, T> exitComponent;

    public DataNode(DATA originalData) {
        this.originalData = originalData;
    }

    public DATA getOriginalData() {
        return originalData;
    }

    public EVAL getCopy() {
        return copy;
    }

    @Nullable
    public ComponentNode<DATA, EVAL, T> getComponent(ComponentNode.ComponentType type) {
        return this.components.get(type);
    }

    public Set<ComponentNode<DATA, EVAL, T>> getComponents() {
        return Set.copyOf(this.components.values());
    }

    @Override
    public @NotNull OperationNode<EVAL> getEntry() {
        return this.entryComponent.getEntry();
    }

    @Override
    public @NotNull OperationNode<EVAL> getExit() {
        return this.exitComponent.getExit();
    }

    public void addComponent(ComponentNode<DATA, EVAL, T> componentNode) {
        if (this.components.containsValue(componentNode) || this.components.containsKey(componentNode.getType())) {
            throw new IllegalArgumentException("Component already present in data node.");
        }

        if (componentNode.getOwner() != this) {
            throw new IllegalArgumentException("Component node does not belong to this data node.");
        }

        DataScope nodeScope;
        if ((nodeScope = componentNode.getMergedScope()) != null) {
            for (ComponentNode<DATA, EVAL, T> otherNode : this.components.values()) {
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

    public void setEntry(@NotNull ComponentNode<DATA, EVAL, T> entry) {
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

    public void setExit(@NotNull ComponentNode<DATA, EVAL, T> exit) {
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
