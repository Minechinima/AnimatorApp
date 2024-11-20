package com.minechinima.model.memory;

import com.minechinima.model.DataTreeManager;
import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.exceptions.ContractViolatedException;

import java.util.*;

/**
 * A central memory management system that allows to register when something references a {@link BaseStructure} node.
 * <br><br>
 * Anything that is part of this reference management must allow clearing the reference when
 * the node is deleted from the data tree.
 */
public class ReferenceManager {
    private final BaseHierarchyRoot root;
    //TODO we need to record a way to undo the clearing of references, in case controller reverts a transaction because error
    // The references need to be virtually cleared so for a running transaction the app can work with correct states as if the references were removed.
    private final Map<BaseStructure, Set<BaseStructureReferences>> references = new IdentityHashMap<>();

    public ReferenceManager(BaseHierarchyRoot root) {
        this.root = root;
    }

    @SubscribeEvent(type= SubscribeEvent.Type.EVENTS_IN_CHILDREN)
    private void onNodeRemoveFromTree(RemoveFromTreeEvent event) {
        this.clearReferencesTo(event.getTrigger());
    }

    /**
     * Method for {@link BaseStructureReferences} to add itself when a reference is created.
     * @param referencedNode
     * @param reference
     * @implNote package private so only {@link BaseStructureReferences} can call it.
     * The Goal is to make this more secure, so only {@link BaseStructureReferences}
     * can manage it and no external system could break it.
     */
    final void registerReference(BaseStructure referencedNode, BaseStructureReferences reference) {
        if (!reference.containsReference(referencedNode)) {
            throw new ContractViolatedException("Tried registering a reference to a node that is not referenced by the holder.");
        }

        if (!referencedNode.isChildOf(this.root) && this.root != referencedNode) {
            throw new IllegalArgumentException("Cannot manage a reference to a node that is not in the observed tree.");
        }

        Set<BaseStructureReferences> references;
        if ((references = this.references.get(referencedNode)) == null) {
            this.references.put(referencedNode, references = Collections.newSetFromMap(new IdentityHashMap<>()));
        }

        references.add(reference);
    }

    /**
     * Method for {@link BaseStructureReferences} to remove itself from the references when the reference is cleared.
     * @param referencedNode
     * @param reference
     * @implNote package private so only {@link BaseStructureReferences} can call it.
     * The Goal is to make this more secure, so only {@link BaseStructureReferences}
     * can manage it and no external system could break it.
     */
    final void removeReference(BaseStructure referencedNode, BaseStructureReferences reference) {
        if (reference.containsReference(referencedNode)) {
            throw new MemoryLeakError("Tried removing a reference to a node that is still referenced.");
        }

        Set<BaseStructureReferences> references;
        if ((references = this.references.get(referencedNode)) == null) return;
        references.remove(reference);
    }

    private void clearReferencesTo(BaseStructure structure) {
        Set<BaseStructureReferences> references;
        if ((references = this.references.get(structure)) == null) return;

        for (BaseStructureReferences reference : references) {
            try {
                reference.prepareClearReferences(structure);
                reference.finalizeClearReferences();
            } catch (Exception e) {
                throw new MemoryLeakError("An unexpected Exception happened while clearing the reference", e);
            }

            if (reference.containsReference(structure)) {
                throw new MemoryLeakError("Could not clear the reference.");
            }
        }
    }
}
