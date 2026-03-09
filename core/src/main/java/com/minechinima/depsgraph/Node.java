package com.minechinima.depsgraph;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * All node types extend from this.
 * Every node has an entry and exit operation, for relation building.
 */
public abstract class Node {
    private final Set<Relation> inlinks = new HashSet<>();
    private final Set<Relation> outlinks = new HashSet<>();

    /**
     * Creates a relation from this node to the given node.
     * The relations are mapped in the following way: this Exit operation -> to Entry operation.
     * If this or the other nodes are already operation nodes, the mapping will be directly between the operation nodes,
     * which allows to have relations between individual operations instead of just between the entry and exit of the nodes.
     * @param to
     * @param relationName
     */
    public void addFromRelationTo(Node to, String relationName) {
        Relation relation = this.createRelation(this, to, relationName);
        relation.getFrom().addOutRelation(relation);
        relation.getTo().addInRelation(relation);
    }

    /**
     * Creates a relation from this node to the given node.
     * The relations are mapped in the following way: this Exit operation -> to Entry operation.
     * If this or the other nodes are already operation nodes, the mapping will be directly between the operation nodes,
     * which allows to have relations between individual operations instead of just between the entry and exit of the nodes.
     * <br><br>
     * Assigns a default name that uses the node identifier.
     * @param to
     */
    public void addFromRelationTo(Node to) {
        //TODO interface for node to provide identifier
        Relation relation = this.createRelation(this, to, "relationName");
        relation.getFrom().addOutRelation(relation);
        relation.getTo().addInRelation(relation);
    }

    private Relation createRelation(Node from, Node to, String name) {
        OperationNode<?> toEntry = to.getOpNodeForInlink();
        OperationNode<?> fromExit = from.getOpNodeForOutlink();

        if (toEntry == fromExit) {
            throw new IllegalStateException("Cannot create relation from a node to itself.");
        }

        if (toEntry == null || fromExit == null) {
            throw new IllegalStateException("Both nodes must have entry and exit operations to build a relation.");
        }

        return new Relation(fromExit, toEntry, name);
    }

    /**
     * @return which node to use for relations going into this node.
     *         Usually the entry operation node is used. Can be overridden to use a different node.
     *         ExternalNode -> thisNode (use the result of this method for the relation)
     */
    @NotNull
    protected OperationNode<?> getOpNodeForInlink() {
        return this.getEntry();
    }

    /**
     * @return which node to use for relations going out this node.
     *         Usually the exit operation node is used. Can be overridden to use a different node.
     *         thisNode (use the result of this method for the relation) -> ExternalNode
     */
    @NotNull
    protected OperationNode<?> getOpNodeForOutlink() {
        return this.getExit();
    }

    public final Set<Relation> getInlinks() {
        return new HashSet<>(this.inlinks);
    }

    public final Set<Relation> getOutlinks() {
        return new HashSet<>(this.outlinks);
    }

    public boolean hasRelationTo(Node to) {
        return this.outlinks.contains(this.createRelation(this, to, ""));
    }

    protected void addOutRelation(Relation relation) {
        if (relation.getFrom() != this) throw new IllegalArgumentException("Relation from node does not match this node.");
        this.outlinks.add(relation);
    }

    protected void addInRelation(Relation relation) {
        if (relation.getTo() != this) throw new IllegalArgumentException("Relation from node does not match this node.");
        this.inlinks.add(relation);
    }

    /**
     * @return the entry operation of this. Important for building relations.
     */
    @NotNull
    public abstract OperationNode<?> getEntry();

    /**
     * @return the exit operation of this. Important for building relations.
     */
    @NotNull
    public abstract OperationNode<?> getExit();

    @Override
    public String toString() {
        return "Node{" +
                "inlinks=" + inlinks +
                ", outlinks=" + outlinks +
                '}';
    }
}
