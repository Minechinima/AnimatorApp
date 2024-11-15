package com.minechinima.model.base;

import org.jetbrains.annotations.NotNull;

/**
 * Common methods of BaseValueHierarchy subclasses that are dynamic, i.e. allow runtime changes.
 * This is similar to {@link java.util.Collection} interface.<br>
 * However, some methods of that interface are not needed and {@link #clear()}
 * needs a boolean return value.
 * @param <T>
 */
public interface IBaseValueCollection<T extends BaseStructure> {
    int size();
    boolean isEmpty();
    boolean contains(T o);
    /**
     *
     * @param e
     * @return true if the element could be added to the collection.
     */
    boolean add(@NotNull T e);
    /**
     * @param o
     * @return true if the provided element was contained in the collection.
     */
    boolean remove(T o);
    /**
     * @return true if the collection could be completely cleared.
     */
    boolean clear();
}
