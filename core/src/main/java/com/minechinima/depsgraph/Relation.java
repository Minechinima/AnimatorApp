package com.minechinima.depsgraph;

public class Relation {
    private final Node from;
    private final Node to;
    private final String name;

    public Relation(Node from, Node node, String name) {
        this.from = from;
        to = node;
        this.name = name;
    }
}
