package com.minechinima.depsgraph;

import java.util.Objects;

//TODO this might be good place for the strategy when to mark a node for update -> e.g. what data has to change so the topology changes.
// maybe store an optional datascope here -> every data gets a change event listener that will mark the relation to be rebuilt
// for every node that gets added the depsgraph manager creates a listener. When change event is fired, relation is marked
// for the next update cycle to be rebuilt. both dataNodes are removed and all relations to it.
public class Relation {
    private final Node from;
    private final Node to;
    private final String name;

    public Relation(Node from, Node node, String name) {
        this.from = from;
        this.to = node;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Node getTo() {
        return this.to;
    }

    public Node getFrom() {
        return this.from;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Relation relation)) return false;
        return Objects.equals(from, relation.from) && Objects.equals(to, relation.to);
    }

    /**
     * Hashcode is only based on from and to, to ensure only one relation between two nodes, even if they have different names.
     * @return
     */
    @Override
    public final int hashCode() {
        return Objects.hash(from, to);
    }

    @Override
    public String toString() {
        return "Relation{" +
                "from=" + from +
                ", to=" + to +
                ", name='" + name + '\'' +
                '}';
    }
}
