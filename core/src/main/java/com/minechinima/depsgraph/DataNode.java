package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;

import java.util.HashMap;
import java.util.Map;

/**
 * One node represents one datastructure to update. A data node is further structured into
 * sub components for different aspects of the data, see {@link ComponentNode}.
 *
 * @param <T>
 */
public class DataNode<T extends BaseStructure> extends Node {
    private final T originalData;
    private T copy;
    private final Map<ComponentNode.IKey, ComponentNode<T>> components = new HashMap<>();

    public DataNode(T originalData) {
        this.originalData = originalData;
    }
}
