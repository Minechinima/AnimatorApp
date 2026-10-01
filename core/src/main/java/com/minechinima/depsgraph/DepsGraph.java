package com.minechinima.depsgraph;

import com.minechinima.depsgraph.tagging.INodeTag;
import com.minechinima.depsgraph.tagging.NodeTagStore;
import com.minechinima.depsgraph.tagging.NodeTaggingManager;
import com.minechinima.model.api.IData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Datastructure of one dependency graph, managing its topology and update logic.
 * Is decoupled from database.
 */
public class DepsGraph {
    private final Map<IData, DataNode> dataNodes = new HashMap<>();
    private final Set<OperationNode> operationNodes = new HashSet<>();
    /**
     * External storage of tagged nodes, to allow for efficient lookup and keeping tagging logic decoupled from the graph topology.
     */
    private final NodeTagStore tagStore = new NodeTagStore();
    protected final NodeTaggingManager taggingManager = new NodeTaggingManager(this);

    private final HashMap<Class<? extends IData>, Set<DataNode<?, ?, ?>>> dataNodesByClass = new HashMap<>();
    //TODO mappings for datascopes and components in efficient way for lookup
    // TODO investigate whether 1-1 mapping for data is really necessary. Blender uses this, but blender is very explicit with declarations and logic
    //  blender uses it to get targets for animation data to tell the system: when the property is animated - also update the other components

    public void addDataNode(DataNode<?, ?, ?> node) {
    }

    public void removeDataNode(DataNode<?, ?, ?> node) {
        // Collect every node managed by this DataNode (bottom-up: operations → components → datanode)
        Set<Node> allNodes = new HashSet<>();

        for (ComponentNode<?, ?, ?> component : node.getComponents()) {
            for (OperationNode opNode : component.getOperations().values()) {
                allNodes.add(opNode);
            }
            allNodes.add(component);
        }
        allNodes.add(node);

        // Clear tags and remove all relations for every managed node
        for (Node n : allNodes) {
            this.tagStore.clearNode(n);
            n.removeAllRelations();
        }

        // Remove from internal collections
        this.dataNodes.remove(node.getOriginalData());

        Set<DataNode<?, ?, ?>> classSet = this.dataNodesByClass.get(node.getOriginalData().getClass());
        if (classSet != null) {
            classSet.remove(node);
            if (classSet.isEmpty()) {
                this.dataNodesByClass.remove(node.getOriginalData().getClass());
            }
        }

        for (ComponentNode<?, ?, ?> component : node.getComponents()) {
            for (OperationNode<?> opNode : component.getOperations().values()) {
                this.operationNodes.remove(opNode);
            }
        }

        this.taggingManager.unregister(node);
    }

    /**
     * Get the data node for the given data and supertype. Checks if the datanode supertype is assignable to the given supertype.
     * This helps with type safety so the caller can infer the types.
     * @param data
     * @param superType
     * @return
     * @param <DATA>
     * @param <T>
     */
    @Nullable
    public <DATA extends T, T extends IData> DataNode<DATA, ?, T> getDataNodeForData(DATA data, Class<T> superType) {
        //TODO lookup via datascopes
        return null;
    }

    /**
     * Get the data node for the given data. The supertype is unknown here.
     * @param data
     * @return
     * @param <DATA>
     * @param <T>
     */
    @Nullable
    public <DATA extends T, T extends Object & IData> DataNode<DATA, ?, T> getDataNodeForData(DATA data) {
        //TODO lookup via datascopes
        return null;
    }

    /**
     * Returns any kind of node that is the lowest level node containing the data in its
     * data scope.
     * This can be used to build relations between nodes for data updates.
     * @param data
     * @return
     */
    @Nullable
    public Node getLowestNodeForDataAttribute(IData data) {
        return null;
    }

    /**
     * Returns the datanode that is a distant owner of the data attribute.
     * @param data
     * @return
     */
    @Nullable
    public DataNode getDataNodeForDataAttribute(IData data) {
        return null;
    }

    @Nullable
    public <DATA extends T, T extends IData> T getEvaluatedData(DATA data, Class<T> superType) {
        // TODO search for the datanode and check if the type is assignable to the supertype.
        return null;
    }

    public Set<OperationNode> getOperationNodes() {
        return new HashSet<>(this.operationNodes);
    }

    // ---- Tag delegation ----

    /**
     * Tag a node with the given tag.
     */
    public void tag(Node node, INodeTag tag) {
        //TODO check if the node is in this depsgraph.
        // cache all nodes. Useful for removal later too
        this.tagStore.tag(node, tag);
    }

    /**
     * Remove a specific tag from a node.
     */
    public void untag(Node node, INodeTag tag) {
        this.tagStore.untag(node, tag);
    }

    /**
     * @return true if the node currently carries the given tag
     */
    public boolean isTagged(Node node, INodeTag tag) {
        return this.tagStore.isTagged(node, tag);
    }

    /**
     * @return all nodes that currently carry the given tag
     */
    public Set<Node> getTaggedNodes(INodeTag tag) {
        return this.tagStore.getTaggedNodes(tag);
    }

    /**
     * Remove all tags from all nodes.
     */
    public void clearAllTags() {
        this.tagStore.clearAll();
    }

    /**
     * Remove the tags from the node.
     *
     * @param node the node to clear all tags from
     */
    public void clearTags(Node node) {
        this.tagStore.clearNode(node);
    }
}
