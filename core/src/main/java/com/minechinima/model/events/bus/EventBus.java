package com.minechinima.model.events.bus;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.types.Event;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The base EventBus class for a {@link BaseStructure} node to dispatch events through.
 * An EventBus always belongs to one {@link BaseStructure} node.
 * <br><br>
 * The only purpose of this bus it dispatch events through, no management of listeners or anything else.
 * <br><br>
 * This acts as a bridge between the secure package-private definitions in this package
 * and the implementations that want to dispatch events.
 * <br><br>
 * You need to extend this class
 * and open the {@link #dispatchEventFromChildNode(Event)} and {@link #dispatchEventOnThis(Event)}
 * methods.
 * Ideally, they get the default access-modifier to further restrict who can dispatch events through a bus.
 */
public abstract class EventBus {
    /**
     * The EventBusManager that manages this EventBus. The manager is what receives the events,
     * as it manages the listeners.
     * <br><br>
     * @implNote Do not expose this attribute.
     */
    private final Set<EventBusManager> managers = new HashSet<>();
    /**
     * Important to define what the owner of the eventbus is, so no one else
     * can dispatch events through it.
     * <br><br>
     * An EventBus is always a 1:1 relationship for a node.
     */
    private final BaseStructure owner;

    /**
     * @param owner owner of this eventbus
     * @implNote protected so only the owner can instantiate it.
     */
    protected EventBus(@NotNull BaseStructure owner) {

        this.owner = Objects.requireNonNull(owner);
    }

    public BaseStructure getOwner() {
        return owner;
    }

    /**
     * @param manager
     * @implNote Package-private so only the {@link EventBusManager} can set the manager. This makes it more secure against
     * bad manager changes and memory leaks, in theory...
     */
    final void addManager(EventBusManager manager) {
        this.managers.add(manager);
    }

    /**
     * @param manager
     * @implNote Package-private so only the {@link EventBusManager} can remove itself.
     */
    final void removeManager(EventBusManager manager) {
        this.managers.remove(manager);
    }

    /**
     * Dispatch an event that happened on this node.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventOnThis(Event event) {
        if (this.managers.isEmpty()) return;

        if (event.getTrigger() != this.owner) {
            throw new IllegalArgumentException("Cannot dispatch an event with a different trigger node than the bus owner.");
        }
        this.managers.forEach(m -> m.dispatchEvent(this, EventOrigin.NODE, event));
    }

    /**
     * Dispatch an event that happened in the child nodes of this EventBus owner.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventFromChildNode(Event event) {
        if (this.managers.isEmpty()) return;

        if (!(this.owner instanceof BaseValueHierarchy<?> hierarchy) || !event.getTrigger().isChildOf(hierarchy)) {
            throw new IllegalArgumentException("Cannot dispatch a child event from a node that is not child of the bus owner.");
        }
        this.managers.forEach(m -> m.dispatchEvent(this, EventOrigin.CHILDREN, event));
    }
}
