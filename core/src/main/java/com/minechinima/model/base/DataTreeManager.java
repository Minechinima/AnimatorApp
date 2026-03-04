package com.minechinima.model.base;

import com.minechinima.controller.ControllerFacade;
import com.minechinima.model.events.bus.EventBusManager;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.memory.ReferenceManager;
import org.jetbrains.annotations.NotNull;

/**
 * This class represents the entire database and has important management code.
 */
public class DataTreeManager<T extends BaseHierarchyRoot> {
    private final ReferenceManager referenceManager;
    private final EventBusManager eventBusManager;
    private final T root;
    //TODO spreading this object isnt nice rn, what if we later want to replaced controllermanager at runtime, how to handle this
    /**
     * @implNote do not expose, use the treemanager as wrapper so BaseStructure only depends on model API.
     */
    private ControllerFacade controllerFacade;

    public DataTreeManager(@NotNull T root, EventTypeRegistry eventRegistry) {
        this.root = root;
        this.root.setDataTreeManager(this);
        this.referenceManager = new ReferenceManager(root);
        this.eventBusManager = new EventBusManager();
        //this.referenceManager.initEventSubscription(this.eventBusManager);
        //this.getEventBusManager().subscribe(this.root.getEventBus(), this);

        this.root.getEventBus().subscribe(this);
    }

    //TODO setter not appropiate for a connector object between to layers, vulnerable to changing and decoupling from controller
    public void setControllerFacade(ControllerFacade controllerFacade) {
        this.controllerFacade = controllerFacade;
    }

    public boolean canMutate() {
        return this.controllerFacade == null ? true : this.controllerFacade.canMutate();
    }

    @SubscribeEvent(type = SubscribeEvent.Type.IN_CHILDREN)
    private void onRemove(RemoveFromTreeEvent event) {
        this.removeDataTreeManagerToSubTree(event.getTrigger());
    }

    @SubscribeEvent(type = SubscribeEvent.Type.IN_CHILDREN)
    private void onAdd(AddToTreeEvent event) {
        this.addDataTreeManagerToSubTree(event.getTrigger());
    }

    private void removeDataTreeManagerToSubTree(BaseStructure node) {
        node.setDataTreeManager(null);

        if (node instanceof BaseValueHierarchy<?> baseValueHierarchy) {
            baseValueHierarchy.getChildren().forEach(this::removeDataTreeManagerToSubTree);
        }
    }

    private void addDataTreeManagerToSubTree(BaseStructure node) {
        node.setDataTreeManager(this);

        if (node instanceof BaseValueHierarchy<?> baseValueHierarchy) {
            baseValueHierarchy.getChildren().forEach(this::addDataTreeManagerToSubTree);
        }
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
