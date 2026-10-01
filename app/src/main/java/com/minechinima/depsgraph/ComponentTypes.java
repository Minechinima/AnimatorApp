package com.minechinima.depsgraph;

import com.minechinima.depsgraph.ComponentNode.ComponentType;

public enum ComponentTypes implements ComponentType {
    TRANSFORM,
    GEOMETRY,
    CONSTRAINTS;

    @Override
    public String getName() {
        return this.name().toUpperCase();
    }
}
