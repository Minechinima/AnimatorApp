package com.minechinima.depsgraph.tagging;

import com.minechinima.depsgraph.DepsGraph;
import com.minechinima.depsgraph.Node;

import java.util.*;

/**
 * Single-threaded storage for {@link INodeTag} labels attached to {@link Node}s.
 */
public class NodeTagStore {
    /**
     * Identity-based map so that the same node instance is always resolved correctly,
     * regardless of equals/hashCode overrides.
     */
    private final Map<Node, Set<INodeTag>> tags = new IdentityHashMap<>();

    /**
     * Tag a node with the given tag.
     *
     * @param node the node to tag
     * @param tag  the tag to apply
     */
    public void tag(Node node, INodeTag tag) {
        this.tags.computeIfAbsent(node, k -> new HashSet<>()).add(tag);
    }

    /**
     * Remove a specific tag from a node.
     *
     * @param node the node to untag
     * @param tag  the tag to remove
     */
    public void untag(Node node, INodeTag tag) {
        Set<INodeTag> nodeTags = this.tags.get(node);
        if (nodeTags != null) {
            nodeTags.remove(tag);
            if (nodeTags.isEmpty()) {
                this.tags.remove(node);
            }
        }
    }

    /**
     * @param node the node to check
     * @param tag  the tag to check for
     * @return true if the node currently carries the given tag
     */
    public boolean isTagged(Node node, INodeTag tag) {
        Set<INodeTag> nodeTags = this.tags.get(node);
        return nodeTags != null && nodeTags.contains(tag);
    }

    /**
     * Returns all nodes that currently carry the given tag.
     *
     * @param tag the tag to search for
     * @return a new set of nodes tagged with the given tag (never null)
     */
    public Set<Node> getTaggedNodes(INodeTag tag) {
        Set<Node> result = new HashSet<>();
        this.tags.forEach((node, nodeTags) -> {
            if (nodeTags.contains(tag)) {
                result.add(node);
            }
        });
        return result;
    }

    /**
     * Remove all tags from all nodes.
     */
    public void clearAll() {
        this.tags.clear();
    }

    /**
     * Remove all tags from a specific node.
     * Also removes the node from internal storage.
     *
     * @param node the node to clear
     */
    public void clearNode(Node node) {
        this.tags.remove(node);
    }
}

