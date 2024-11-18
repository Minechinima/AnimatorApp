package com.minechinima.model.base.utils;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.dataoperations.datainterfaces.IDMutable;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class BaseOperationsUtils {
    /**
     * Utility method to encapsulate all the exceptions that need to be thrown.
     * <br><br>
     * The following cases are not allowed for adding a child and will throw an exception:
     * <ul>
     *     <li>Child is null.</li>
     *     <li>Child is already contained in the children (reference equality)</li>
     *     <li>Child is {@link IDMutable} and the UUID is already contained in the children.</li>
     *     <li>Cannot add the child to the collection of children.</li>
     *     <li>Cannot set the parent.</li>
     * </ul>
     *
     * @param children the internal collection of children in the parent.
     * @throws RuntimeException
     */
    public static <T extends BaseStructure> void addChildOrThrow(T child,
                                                                 BaseValueHierarchy<T> parent,
                                                                 List<T> children, int index) throws RuntimeException {
        throwExceptionsIllegalChild(child, parent);

        children.add(index, child);

        setNewParentOrThrow(child, parent, children);
    }

    /**
     * Utility method to encapsulate all the exceptions that need to be thrown.
     * <br><br>
     * The following cases are not allowed for adding a child and will throw an exception:
     * <ul>
     *     <li>Child is null.</li>
     *     <li>Child is already contained in the children (reference equality)</li>
     *     <li>Child is {@link IDMutable} and the UUID is already contained in the children.</li>
     *     <li>Cannot add the child to the collection of children.</li>
     *     <li>Cannot set the parent.</li>
     * </ul>
     *
     * @param children the internal collection of children in the parent.
     * @throws RuntimeException
     */
    public static <T extends BaseStructure> void addChildOrThrow(T child,
                                                                 BaseValueHierarchy<T> parent,
                                                                 Collection<T> children) throws RuntimeException {
        throwExceptionsIllegalChild(child, parent);

        if (!children.add(child)) {
            throw new UnsupportedOperationException("Could not add child to the children Collection.");
        }

        setNewParentOrThrow(child, parent, children);
    }

    private static <T extends BaseStructure> void setNewParentOrThrow(T child,
                                                                        @NotNull BaseValueHierarchy<T> parent,
                                                                        Collection<T> children) throws RuntimeException {
        try {
            child.setParent(parent);
        } catch (SetParentNotAllowed e) {
            if (!children.remove(child)) {
                /* we cannot recover, the program needs to crash, so throw an Error */
                throw new Error("Could not recover from failed parent change.", e);
            }
            throw new RuntimeException(e);
        } catch (Exception e) {
            /*
             * Method contract of setParent tells us that it will always throw SetParentNotAllowed.
             * A different exception might hint at a critical bug that should not be caught!
             */
            //TODO Error classes
            throw new Error("An unexpected exception happened during setting parent", e);
        }
    }

    private static <T extends BaseStructure> void throwExceptionsIllegalChild(T child,
                                                                             BaseValueHierarchy<T> parent) {
        if (child == null) {
            throw new IllegalArgumentException("A BaseValueHierarchy does not permit null values.");
        }

        for (T element : parent.getChildren()) {
            if (element == child) {
                throw new IllegalArgumentException("A BaseValueHierarchy does not permit reference equal duplicates.");
            }
        }

        if (child instanceof IDMutable insertMutable && parent.isUUIDPresent(insertMutable.getUUID())) {
            throw new UnsupportedOperationException("Cannot add the child as the UUID of the child is already present.");
        }
    }

    public static <T extends BaseStructure> void removeChildOrThrow(T child, Collection<T> children) {
        if (!children.contains(child)) return;

        if (!children.remove(child)) {
            throw new UnsupportedOperationException("Cannot remove child from children collection");
        }

        try {
            child.setParent(null);
        } catch (SetParentNotAllowed e) {
            if (!children.add(child)) {
                /* we cannot recover, the program needs to crash, so throw an Error */
                throw new Error("Could not recover from failed parent change.");
            }
            throw new UnsupportedOperationException(e);
        }  catch (Exception e) {
            /*
             * Method contract of setParent tells us that it will always throw SetParentNotAllowed.
             * A different exception might hint at a critical bug that should not be caught!
             */
            //TODO Error classes
            throw new Error("An unexpected exception happened during setting parent", e);
        }
    }

    /**
     * For List, it will try and add the child back to the original index if the operation failed.
     * @param child
     * @param children
     * @param <T>
     */
    public static <T extends BaseStructure> void removeChildOrThrow(T child, List<T> children) {
        int i = children.indexOf(child);
        if (i < 0) return;
        if (!children.remove(child)) {
            throw new UnsupportedOperationException("Cannot remove child from children collection");
        }

        try {
            child.setParent(null);
        } catch (SetParentNotAllowed e) {
            children.add(i, child);
            throw new UnsupportedOperationException(e);
        }  catch (Exception e) {
            /*
             * Method contract of setParent tells us that it will always throw SetParentNotAllowed.
             * A different exception might hint at a critical bug that should not be caught!
             */
            //TODO Error classes
            throw new Error("An unexpected exception happened during setting parent", e);
        }
    }
}
