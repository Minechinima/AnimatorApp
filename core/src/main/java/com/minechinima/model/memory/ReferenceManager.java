package com.minechinima.model.memory;

import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.EventBusManager;
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
    private final Map<BaseStructure, Set<BaseStructureReferences>> cachedDeletion = new IdentityHashMap<>();
    private boolean cacheDeletion = false;

    public ReferenceManager(BaseHierarchyRoot root) {
        this.root = root;
    }

    public void initEventSubscription(EventBusManager busManager) {
        busManager.subscribe(this.root.getEventBus(), this);
    }

    @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
    private void onNodeRemoveFromTree(RemoveFromTreeEvent event) {
        BaseStructure removedNode = event.getTrigger();
        Set<BaseStructureReferences> references;
        if ((references = this.references.get(removedNode)) == null) return;

        if (this.cacheDeletion) {
            this.cachedDeletion.put(removedNode, references);
        }

        for (BaseStructureReferences reference : references) {
            try {
                reference.prepareClearReferences(removedNode);
                if (!this.cacheDeletion) {
                    reference.finalizeClearReferences();

                    if (reference.containsReference(removedNode)) {
                        throw new MemoryLeakError("Could not clear the reference.");
                    }
                }
            } catch (Exception e) {
                throw new MemoryLeakError("An unexpected Exception happened while clearing the reference", e);
            }
        }

        this.references.remove(removedNode);
    }

    public void cacheClearedReferences() {
        this.cacheDeletion = true;
    }

    public void reapplyCachedClearedReferences() {
        this.cacheDeletion = false;

        for (Map.Entry<BaseStructure, Set<BaseStructureReferences>> ref : this.cachedDeletion.entrySet()) {
            BaseStructure referenced = ref.getKey();
            if (!referenced.isChildOf(this.root) && referenced != this.root) {
                throw new MemoryLeakError("Tried reapplying a reference to a node that is not part of the tree");
            }

            for (BaseStructureReferences reference : ref.getValue()) {
                try {
                    reference.undoClearReferences();

                    if (!reference.containsReference(referenced)) {
                        throw new MemoryLeakError("Could not reapply the reference.");
                    }
                } catch (Exception e) {
                    throw new MemoryLeakError("An unexpected Exception happened while reapplying references.", e);
                }
            }

            this.references.put(ref.getKey(), ref.getValue());
        }

        this.cachedDeletion.clear();
    }

    public void finalizeCachedClearedReferences() {
        this.cacheDeletion = false;

        for (Map.Entry<BaseStructure, Set<BaseStructureReferences>> ref : this.cachedDeletion.entrySet()) {
            BaseStructure referenced = ref.getKey();
            for (BaseStructureReferences reference : ref.getValue()) {
                try {
                    reference.finalizeClearReferences();

                    if (reference.containsReference(referenced)) {
                        throw new MemoryLeakError("Could not clear the reference.");
                    }
                } catch (Exception e) {
                    throw new MemoryLeakError("An unexpected Exception happened while clearing the reference", e);
                }
            }
        }

        this.cachedDeletion.clear();
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

    /**
     * Fully remove the reference from this.
     * This is useful when you destroy the data structure that references nodes.
     * @param reference
     * @implNote package private so only {@link BaseStructureReferences} can call it.
     */
    final void removeReference(BaseStructureReferences reference) {
        Set<BaseStructure> referencedBy = this.getNodesReferencedBy(reference);
        if (referencedBy.isEmpty()) return;

        for (BaseStructure referencedNode : referencedBy) {
            if (reference.containsReference(referencedNode)) {
                throw new MemoryLeakError("Tried removing a reference to a node that is still referenced.");
            }

            this.references.get(referencedNode).remove(reference);
        }
    }

    private Set<BaseStructure> getNodesReferencedBy(BaseStructureReferences referencer) {
        Set<BaseStructure> referenced = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Map.Entry<BaseStructure, Set<BaseStructureReferences>> entry : this.references.entrySet()) {
            if (entry.getValue().contains(referencer)) referenced.add(entry.getKey());
        }

        return referenced;
    }
}
