package com.minechinima.model.utils;

public interface IPointerTarget {
    /**
     * Clears all {@link IPointer} implementations that point to this instance.
     */
    void freePointers();

    void addPointer(IPointer<? extends IPointerTarget> pointer);

    /**
     * Clears the pointer that points to this instance if the given pointer is cached
     * in this instance.
     * @param pointer
     */
    void removePointer(IPointer<? extends IPointerTarget> pointer);
}
