package com.minechinima.model.api;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Read-only API interface for nodes that hold a mutable value.
 * <br><br>
 * This interface provides access to value data without exposing the setter.
 * All value mutations should be performed through the controller factory.
 * <br><br>
 * <b>Design Note:</b> The setter is intentionally not exposed here to enforce
 * that all modifications go through the controller layer for proper event handling,
 * logging, and undo/redo support.
 *
 * @param <T> the type of value held by this node
 * @see IData for core node operations
 * @see IDataTree for hierarchy operations
 */
public interface IValueNode<T> extends IData {

    /**
     * Gets the current value held by this node.
     *
     * @return the value, or null if not set
     */
    @Nullable
    @Contract(pure = true)
    T get();

    /**
     * Gets the default value for this node.
     *
     * @return the default value, or null if no default is set
     */
    @Nullable
    @Contract(pure = true)
    T getDefaultValue();
}

