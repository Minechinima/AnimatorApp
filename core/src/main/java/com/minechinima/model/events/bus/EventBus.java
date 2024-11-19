package com.minechinima.model.events.bus;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.types.Event;
import org.jetbrains.annotations.Nullable;

/**
 * The base EventBus class for a {@link BaseStructure} node to dispatch events through.
 * <br><br>
 * This acts as a bridge between the package-private definitions in this package
 * and the implementations that want to dispatch events.
 * <br><br>
 * You need to extend this class and open the {@link #dispatchEventFromChildNode(Event)} and {@link #dispatchEventOnThis(Event)}
 * methods. Ideally, they get the default access-modifier to further restrict who can dispatch events through a bus.
 */
public abstract class EventBus {
    @Nullable
    private EventBusManager manager;
    /**
     * Important to define what the owner of the eventbus is, so no one else
     * can dispatch events through it.
     */
    private final BaseStructure owner;

    protected EventBus(BaseStructure owner) {
        this.owner = owner;
    }

    public BaseStructure getOwner() {
        return owner;
    }

    /**
     *
     * @param manager
     * @implNote Package-private so only the {@link EventBusManager} can set the manager. This makes it more secure against
     * bad manager changes and memory leaks, in theory...
     */
    final void setManager(EventBusManager manager) {
        EventBusManager prevManager = this.manager;
        this.manager = manager;
        if (prevManager != null && prevManager != manager) {
            prevManager._removeEventBus(this);
        }
    }

    /**
     * Dispatch an event that happened on this node.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     */
    protected final void dispatchEventOnThis(Event event) {
        if (this.manager == null) return;
        if (event.getTrigger() != this.owner) {
            throw new IllegalArgumentException("Cannot dispatch an event with a different trigger node than the bus owner.");
        }
        this.manager.dispatchEvent(this, EventOrigin.NODE, event);
    }

    /**
     * Dispatch an event that happened in the child nodes of this EventBus owner.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     */
    protected final void dispatchEventFromChildNode(Event event) {
        if (this.manager == null) return;

        if (!(this.owner instanceof BaseValueHierarchy<?> hierarchy) || !event.getTrigger().isChildOf(hierarchy)) {
            throw new IllegalArgumentException("Cannot dispatch a child event from a node that is not child of the bus owner.");
        }
        this.manager.dispatchEvent(this, EventOrigin.CHILDREN, event);
    }
}
