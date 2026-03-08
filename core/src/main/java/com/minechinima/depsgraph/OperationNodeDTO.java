package com.minechinima.depsgraph;

import com.minechinima.model.base.BaseStructure;

/**
 * Data Transfer Object passed to an {@link OperationNode}'s eval function.
 * Provides type-safe access to the evaluated copy of the data node's data.
 * <p>
 * The default DTO only provides the root copy via {@link #getCopy()}.
 * Subclass this to declare specific subdata objects that an operation node
 * function needs to receive from the evaluated copy.
 * <p>
 * The DTO only ever holds <b>copies</b>; original database objects
 * are never exposed to the operation node.
 *
 * @param <T> the data node's data type
 */
public class OperationNodeDTO<T extends BaseStructure> {
    private final T copy;

    public OperationNodeDTO(T copy) {
        this.copy = copy;
    }

    /**
     * @return the evaluated copy of the data node's data.
     *         Never the original database object.
     */
    public T getCopy() {
        return copy;
    }
}
