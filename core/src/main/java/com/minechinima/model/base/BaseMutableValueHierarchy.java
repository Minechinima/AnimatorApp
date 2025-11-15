package com.minechinima.model.base;

import com.minechinima.model.utils.ValuePath;

import java.util.UUID;

/**
 * The base for all hierarchies that can change after init.
 * @param <T>
 */
public non-sealed abstract class BaseMutableValueHierarchy<T extends BaseStructure> extends BaseValueHierarchy<T> {
    public BaseMutableValueHierarchy(String id, ValuePath.IFormat pathFormat) {
        super(id, pathFormat);
    }

    protected BaseMutableValueHierarchy(String id, UUID uuid, ValuePath.IFormat pathFormat) {
        super(id, uuid, pathFormat);
    }

    /**
     * Remove the given child in this.
     * @param child
     * @return true when the child was present and could be removed.
     *         false if the child was not present or if it could not be removed.
     */
    public abstract boolean removeChild(T child);
}
