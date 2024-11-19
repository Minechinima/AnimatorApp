package com.minechinima.model.memory;

import com.minechinima.model.base.BaseStructure;

import java.util.Objects;

/**
 * This class represents a datastructure, simple variable or complex datastructures, which
 * can reference {@link BaseStructure} nodes and wants to participate in the automatic cleanup of references when such
 * a node is removed from the tree.
 */
public abstract class BaseStructureReferences {
    /**
     * The manager that is responsible for managing this reference.
     */
    private final ReferenceManager manager;

    public BaseStructureReferences(ReferenceManager manager) {
        this.manager = Objects.requireNonNull(manager);
    }

    /**
     * This is called when the datastructure <b>itself</b> references a new node.
     * @param reference
     */
    protected final void afterAddReference(BaseStructure reference) {
        this.manager.registerReference(reference, this);
    }

    /**
     * This is called when the datastructure <b>itself</b> removes the reference.
     * @param reference
     */
    protected final void afterRemoveReference(BaseStructure reference) {
        this.manager.removeReference(reference, this);
    }

    /**
     * Virtually removes references in this datastructure to the specified {@link BaseStructure} node.
     * <br><br>
     * <b>Important that this is only virtual, as we need to later call {@link #finalizeClearReferences()} or be able to
     * undo the clearing with {@link #undoClearReferences()}</b>
     * @param reference
     */
    public abstract void prepareClearReferences(BaseStructure reference);

    /**
     * Fully remove the reference.
     * @param reference
     * @implNote Differentiating between virtual clear and final clear allows clearing references during a transaction,
     * letting the system believe they are gone so the app can work accordingly, but if the transaction fails, we reapply the references.
     * If the transaction is successful, we finalize the clearing - fully removing it for GC.<br><br>
     * This also allows adding more logic to the final clearing,
     * giving more flexibility to a subclass when it is part of a more complex context.
     */
    public abstract void finalizeClearReferences();

    /**
     * This can be called after {@link #prepareClearReferences(BaseStructure)} to reapply the reference.
     */
    public abstract void undoClearReferences();
    /**
     * <b>Important:</b> This needs to check ALL variables (even temporary caches) if there is a reference.<br>
     * If this contract is broken, it is your fault if memory leaks happen.
     * @return true if the specified {@link BaseStructure} node is referenced ANYWHERE (even removal cache) in this data structure.
     */
    public abstract boolean containsReference(BaseStructure reference);
}
