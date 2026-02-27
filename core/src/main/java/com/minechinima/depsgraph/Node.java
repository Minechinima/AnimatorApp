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

    public void addFromRelationTo(Node to, String relationName) {
        OperationNode<?> toEntry = to.getEntry();
        OperationNode<?> fromExit = this.getExit();

        if (toEntry == null || fromExit == null) {
            throw new IllegalStateException("Both nodes must have entry and exit operations to build a relation.");
        }

        Relation relation = new Relation(fromExit, toEntry, relationName);
        fromExit.addOutRelation(relation);
        toEntry.addInRelation(relation);
    }

    public final Set<Relation> getInlinks() {
        return new HashSet<>(this.inlinks);
    }

    public final Set<Relation> getOutlinks() {
        return new HashSet<>(this.outlinks);
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
}
