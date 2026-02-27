package com.minechinima.depsgraph;

/**
 * Default types
 */
public enum ComponentTypes implements ComponentNode.ComponentType {
    DATA_ENTRY,
    DATA_EXIT;

    @Override
    public String getName() {
        return this.name();
    }
}
