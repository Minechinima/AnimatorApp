package com.minechinima.model.base;

import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.utils.ValuePath;

import java.util.*;

/**
 * A base for all hierarchies that are static and cannot change children after init.
 * @param <T>
 */
public non-sealed abstract class BaseImmutableValueHierarchy<T extends BaseStructure> extends BaseValueHierarchy<T> {
    private final Set<T> children = Collections.newSetFromMap(new IdentityHashMap<>());

    public BaseImmutableValueHierarchy(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected BaseImmutableValueHierarchy(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
    }

    /**
     * @return a copy of the children Set.
     * @implNote  this is final to ensure that the children are always immutable
     */
    @Override
    public final Set<T> getChildren() {
        return new HashSet<>(this.children);
    }

    /**
     * @param child
     * @return true if this child is contained in the children of this.
     * @implNote this is final to ensure that the children are always immutable
     */
    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return this.children.contains(child);
    }

    protected final void addChildren(T... children) {
        for (T child : children) {
            if (this.children.contains(child)) continue;

            if (child instanceof IDMutable mutable && this.isUUIDPresent(mutable.getUUID())) {
                throw new IllegalArgumentException("One of the BaseStructures to be inserted has a UUID " +
                        "that already exists in the children.");
            }

            this.children.add(child);
            try {
                child.setParent(this);
            } catch (SetParentNotAllowed e) {
                throw new RuntimeException(e);
            }
        }
    }
}
