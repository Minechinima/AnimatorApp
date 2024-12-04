package com.minechinima.model.utils;

import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.exceptions.ContractViolatedException;

/**
 * A node, where the direct children are of type {@link com.minechinima.model.base.ValuePointer},
 * can implement this to impose structural restrictions on what kind of pointer values are allowed.
 * <br><br>
 * This will be tested before actually setting the pointer to the new value.
 * This allows preventing the tree from ever having pointer values that are structurally not allowed.
 */
public interface IValuePointerConstraint {
    boolean nullPointerAllowed();

    /**
     * Only called when {@link #nullPointerAllowed()} is false.
     * Called when the pointer is going to change to a null pointer.
     * @param pointer
     */
    default void callbackOnNullPointer(ValuePointer<?> pointer) {
        throw new ContractViolatedException("Not implemented");
    }

    boolean duplicatePointerTargetsAllowed();

    /**
     * Only called when {@link #duplicatePointerTargetsAllowed()} is false.
     * Called when the pointer is going to change to a reference that is already in a pointer
     * of the children of this node.
     * @param pointer
     */
    default void callbackOnDuplicatePointerTarget(ValuePointer<?> pointer) {
        throw new ContractViolatedException("Not implemented");
    }
}
