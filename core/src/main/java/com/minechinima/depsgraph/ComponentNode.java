package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;

import java.util.HashMap;
import java.util.Map;

public class ComponentNode<T extends BaseStructure> extends Node {
    private final DataNode<T> owner;
    private final OperationNode<T> entry;
    private final OperationNode<T> exit;
    private final Map<OperationNode.OperationCode, OperationNode<T>> operations = new HashMap<>();

    public ComponentNode(DataNode<T> owner, OperationNode<T> entry, OperationNode<T> exit) {
        if (entry == exit) throw new IllegalArgumentException("Entry and exit OperationNodes cannot be equal");
        this.owner = owner;
        this.entry = entry;
        this.exit = exit;
    }

    public DataNode<T> getOwner() {
        return owner;
    }

    public OperationNode<T> getEntry() {
        return entry;
    }

    public OperationNode<T> getExit() {
        return exit;
    }

    /**
     * @param operation
     * @return true if the operation node instance is already registered.
     */
    public boolean isOperationNodePresent(OperationNode operation) {
        for (OperationNode node : this.operations.values()) {
            if (node == operation) return true;
        }
        return false;
    }

    public OperationNode<T> findOperation(OperationNode.OperationCode key) {
        return this.operations.get(key);
    }

    /**
     * For identifying a Component and looking it up in a hashmap.
     * <br><br>
     * Advised to implement it in an enum.
     */
    public interface IKey {
        String getName();
    }
}
