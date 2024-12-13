package com.minechinima.model;

import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.memory.ReferenceManager;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the entire database and has important management code.
 * <br><br>
 * Builder like pattern - the "build" steps are performed automatically when a node is added to the tree.
 * <br><br>
 * Design Rationale:
 * This completely decouples management from the data tree. A tree node doesn't need any reference/event logic.
 * This increases flexibility and makes BaseStructure cleaner.
 */
public class DataTreeManager<T extends BaseHierarchyRoot> {
    private final ReferenceManager referenceManager;
    private final EventBusManager eventBusManager;
    private final T root;

    public DataTreeManager(@NotNull T root, EventTypeRegistry eventRegistry) {
        this.root = root;
        this.referenceManager = new ReferenceManager(root);
        this.eventBusManager = new EventBusManager(this.referenceManager, this.root, eventRegistry);
        this.referenceManager.initEventSubscription(this.eventBusManager);
        this.getEventBusManager().subscribe(this.root.getEventBus(), this);
    }

    public EventBusManager getEventBusManager() {
        return this.eventBusManager;
    }

    public ReferenceManager getReferenceManager() {
        return this.referenceManager;
    }

    public T getRoot() {
        return root;
    }
}
