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
import java.util.Set;

/**
 * Adapter for {@link DepsGraph} methods, so we can control what access a builder has. For example, builders should not be able to
 * remove nodes.
 */
public class BufferedBuilderHelper {
    private final DepsGraph graph;
    public BufferedBuilderHelper(DepsGraph graph) {
        this.graph = graph;
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
        return this.graph.getLowestNodeForDataAttribute(data);
    }

    public Set<OperationNode> getOperationNodes() {
        return this.graph.getOperationNodes();
    }
}
