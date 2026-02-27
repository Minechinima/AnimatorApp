package com.minechinima.depsgraph;

import java.util.Objects;

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
}
