package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;

import java.util.UUID;

/**
 * The base for all hierarchies that can change after init.
 * @param <T>
 */
public non-sealed abstract class BaseMutableValueHierarchy<T extends BaseStructure> extends BaseValueHierarchy<T> {
    public BaseMutableValueHierarchy(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected BaseMutableValueHierarchy(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    /**
     * Adds the given child into this.
     * @param child
     * @return true if the given child was not present (reference equality) and could be added.
     *         false if the given child was already present or could not be added.
     */
    public abstract boolean addChild(T child);

    /**
     * Remove the given child in this.
     * @param child
     * @return true when the child was present and could be removed.
     *         false if the child was not present or if it could not be removed.
     */
    public abstract boolean removeChild(T child);
}
