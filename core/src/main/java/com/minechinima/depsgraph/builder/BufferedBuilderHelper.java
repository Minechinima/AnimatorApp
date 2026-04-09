package com.minechinima.depsgraph.builder;

import com.minechinima.depsgraph.DataNode;
import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;
import com.minechinima.depsgraph.OperationNode;
import com.minechinima.depsgraph.tagging.NodeTagging;
import com.minechinima.depsgraph.tagging.NodeTaggingManager;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class BufferedBuilderHelper {
    private final DepsGraph graph;
    private final Set<NodeTaggingManager.NodeTaggingCallback<?>> taggingConfigBuffer = new HashSet<>();

    public BufferedBuilderHelper(DepsGraph graph) {
        this.graph = graph;
    }

    public <T extends Node> void addNodeTagging(T node, NodeTagging<T> tagging) {
        this.taggingConfigBuffer.add(new NodeTaggingManager.NodeTaggingCallback<>(node, tagging));
    }

    @Nullable
    public <DATA extends T, T extends IData> DataNode<DATA, ?, T> getDataNodeForData(DATA data, Class<T> superType) {
        return this.graph.getDataNodeForData(data, superType);
    }

    @Nullable
    public <DATA extends T, T extends Object & IData> DataNode<DATA, ?, T> getDataNodeForData(DATA data) {
        return this.graph.getDataNodeForData(data);
    }

    @Nullable
    public Node getNodeForDataTarget(IData data) {
        return this.graph.getNodeForDataTarget(data);
    }

    public Set<OperationNode> getOperationNodes() {
        return this.graph.getOperationNodes();
    }
}
