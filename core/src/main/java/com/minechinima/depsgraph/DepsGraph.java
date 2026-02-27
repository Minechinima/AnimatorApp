package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Datastructure of one dependency graph, managing its topology and update logic.
 * Is decoupled from database.
 */
public class DepsGraph {
    private final Map<BaseStructure, DataNode> dataNodes = new HashMap<>();
    private final Set<OperationNode> operationNodes = new HashSet<>();

    private final HashMap<Class<? extends BaseStructure>, Set<DataNode<? extends BaseStructure>>> dataNodesByClass = new HashMap<>();


    public void addDataNode(DataNode<?> node) {

    }
}
