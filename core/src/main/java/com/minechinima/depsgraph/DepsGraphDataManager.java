package com.minechinima.depsgraph;

/**
 * Manages one dependency graph and updates it when data changes.
 */
public class DepsGraphDataManager {
    private final DepsGraph graph;

    public DepsGraphDataManager(DepsGraph graph) {
        this.graph = graph;
    }
}
