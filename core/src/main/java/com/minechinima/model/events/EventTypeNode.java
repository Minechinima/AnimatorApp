package com.minechinima.model.events;

import com.minechinima.model.events.types.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * For compiling the event hierarchy
 * @param <T>
 */
public class EventTypeNode<T extends Event> {
    /**
     * Parent event type node must contain an event type that is a super class.
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
     * Goes through the event types and super event types and collects all listeners that are inside the filter
     * @param filter
     */
    public Set<Class<? extends T>> collectSubEventTypesOf(Class<? extends Event> type) {
        Set<Class<? extends T>> collectedSubTypes = new HashSet<>();

        //TODO optimize
        if (type.equals(this.eventType) || type.isAssignableFrom(this.eventType)) {
            collectedSubTypes.add(this.eventType);
        }

        for (EventTypeNode<? extends T> subType : this.immediateSubTypes) {
            collectedSubTypes.addAll(subType.collectSubEventTypesOf(type));
        }
        return collectedSubTypes;
    }
}
