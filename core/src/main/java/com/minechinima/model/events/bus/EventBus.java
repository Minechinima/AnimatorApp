package com.minechinima.model.events.bus;

import com.minechinima.eventbus.AbstractConstrainedEventBus;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.types.AbstractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * The base EventBus class for a {@link BaseStructure} node to dispatch events through.
 * An EventBus always belongs to one {@link BaseStructure} node.
 * <br><br>
 * This acts as a bridge between the secure package-private definitions in this package
 * and the implementations that want to dispatch events.
 * <br><br>
 * You need to extend this class
 * and open the {@link #dispatchEventFromChildNode(AbstractEvent)} and {@link #dispatchEventOnThis(AbstractEvent)}
 * methods.
 * Ideally, they get the default access-modifier to further restrict who can dispatch events through a bus.
 * <br><br>
 * Internally uses the general-purpose {@link AbstractConstrainedEventBus} framework.
 * The {@link EventOrigin} enum serves as the constraint context, and {@link SubscribeEvent} is
 * the listener annotation whose {@link SubscribeEvent.Type} field specifies which origins a
 * listener wants to receive events from.
 */
public abstract class EventBus extends AbstractConstrainedEventBus<AbstractEvent, EventOrigin, SubscribeEvent> {
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
        super(AbstractEvent.class, SubscribeEvent.class, SubscribeEvent.Type.class);
        this.owner = Objects.requireNonNull(owner);
    }

    public BaseStructure getOwner() {
        return owner;
    }

    /**
     * Dispatch an event that happened on this node.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not equal to the bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventOnThis(AbstractEvent event) {
        if (event.getTrigger() != this.owner) {
            throw new IllegalArgumentException("Cannot dispatch an event with a different trigger node than the bus owner.");
        }

        dispatch(event, EventOrigin.NODE);
    }

    /**
     * Dispatch an event that happened in the child nodes of this EventBus owner.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventFromChildNode(AbstractEvent event) {
        //TODO possible performance issue: for every parent we traverse the tree to test isChildOf
        if (!(this.owner instanceof BaseValueHierarchy<?> hierarchy) || !event.getTrigger().isChildOf(hierarchy)) {
            throw new IllegalArgumentException("Cannot dispatch a child event from a node that is not child of the bus owner.");
        }

        dispatch(event, EventOrigin.CHILDREN);
    }
}
