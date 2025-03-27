package com.minechinima.model.events;

import com.minechinima.model.events.types.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * For compiling the event hierarchy
 * @param <T>
 */
@Deprecated(forRemoval = true)
public class EventTypeNode<T extends Event> {
    /**
     * Subclasses of this event type.
     */
    protected final Set<EventTypeNode<? extends T>> immediateSubTypes = new HashSet<>();
    protected final Class<T> eventType;

    public EventTypeNode(Set<EventTypeNode<? extends T>> subTypes, Class<T> eventType) {
        this.immediateSubTypes.addAll(subTypes);
        this.eventType = eventType;
    }

    public EventTypeNode(Class<T> eventType) {
        this.eventType = eventType;
    }

    /**
     * Collect all sub event types of the given event type.
     * @param type
     * @return a set of all event classes that are subclasses of the specified event class, including the type itself.
     */
    public <J extends Event> Set<Class<? extends J>> collectSubEventTypesOf(Class<J> type) {
        Set<Class<? extends J>> collectedSubTypes = new HashSet<>();

        //TODO optimize
        if (type.equals(this.eventType) || type.isAssignableFrom(this.eventType)) {
            collectedSubTypes.add((Class<? extends J>) this.eventType);
        }

        for (EventTypeNode<? extends T> subType : this.immediateSubTypes) {
            collectedSubTypes.addAll(subType.collectSubEventTypesOf(type));
        }
        return collectedSubTypes;
    }
}
