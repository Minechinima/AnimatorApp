package com.minechinima.model.api;

import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.logging.DataOperationBus;
import com.minechinima.model.utils.ID;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

/**
 * Read-only API interface for all {@link com.minechinima.model.base.BaseStructure} nodes.
 * <br><br>
 * This interface provides access to core properties and relationships of a structure node
 * without exposing any setters. The database should only be mutated through controller commands.
 * <br><br>
 * This decouples the UI from the complexity of the database layer.
 * <br><br>
 * <b>Design Note:</b> This interface provides a simplified, read-only view of the data tree.
 * All mutation operations should be performed through the controller factory of the underlying implementation.
 */
public interface IData {

    /**
     * Gets the string identifier for this node used in path construction.
     *
     * @return the identifier, never null
     */
    @Contract(pure = true)
    String getIdentifier();

    /**
     * Gets the parent of this node in the tree hierarchy.
     *
     * @return the parent node, or null if this is the root
     */
    @Nullable
    @Contract(pure = true)
    IDataTree<?> getParent();

    /**
     * Gets the root node of the tree containing this node.
     * <br>
     * Note: Implementations should return the root as a read-only IHierarchyNode view.
     * The actual internal return type may be more specific (BaseValueHierarchy).
     *
     * @return the root hierarchy node, or null if this node is not a hierarchy
     */
    @Nullable
    @Contract(pure = true)
    IDataTree<?> getRoot();

    /**
     * Checks if this node is a child (direct or distant) of the given parent.
     *
     * @param parent the potential parent node (should be an IHierarchyNode)
     * @return true if this node is a child of the given parent
     */
    @Contract(pure = true)
    boolean isChildOf(IDataTree<?> parent);

    /**
     * Gets the event bus for this node.
     * The EventBus system is hooked up to the database backend nodes.
     * @return the event bus, never null
     */
    @Contract(pure = true)
    EventBus getEventBus();

    /**
     * Gets the data operation bus for logging data modifications.
     * This bus tracks all mutations performed on this node.
     *
     * @return the data operation bus, never null
     */
    @Contract(pure = true)
    DataOperationBus getDataOperationBus();
}

