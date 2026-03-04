package com.minechinima.model.api;

import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Read-only API interface for hierarchy nodes that contain children in the tree.
 * <br><br>
 * This interface provides access to child nodes and tree traversal operations
 * without exposing any methods to add or remove children.
 * <br><br>
 * <b>Design Note:</b> This is a read-only view of a hierarchy. Use the controller factory
 * to perform mutations on the tree structure (adding/removing children, etc.).
 * @see IData for core node operations
 */
public interface IDataTree extends IData {

    /**
     * Gets all direct children of this data tree node.
     *
     * @return an unmodifiable collection of direct children, never null
     */
    @Contract(pure = true)
    Collection<? extends IData> getChildren();

    /**
     * Checks if a given node is a direct child of this hierarchy.
     *
     * @param child the node to check
     * @return true if the node is a direct child
     */
    @Contract(pure = true)
    boolean isDirectChild(IData child);

    /**
     * Finds a data node that is a direct or distant child of this with the given
     * relative path to this.
     *
     * @param path the parsed path object
     * @return the child at that path, or null if not found
     */
    @Nullable
    @Contract(pure = true)
    IData getChildByValuePath(ValuePath path);

    /**
     * Finds a data node that is a direct or distant child of this with the given
     * relative path to this.
     *
     * @param path the parsed unique path object
     * @return the child at that path, or null if not found
     */
    @Nullable
    @Contract(pure = true)
    IData getChildByUniquePath(ValuePath path);

    /**
     * Checks if a UUID is present among the direct children.
     * <br>
     * This is only relevant for mutable children that implement IDMutable.
     *
     * @param uuid the UUID to search for
     * @return true if a direct child has this UUID
     */
    @Contract(pure = true)
    boolean isUUIDPresent(UUID uuid);
}

