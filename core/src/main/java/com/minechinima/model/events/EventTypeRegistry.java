package com.minechinima.model.events;

import com.minechinima.model.events.types.Event;

import java.util.*;

public class EventTypeRegistry {
    private final Set<Class<? extends Event>> availableEvents = new HashSet<>();

    public EventTypeRegistry() {
        this.availableEvents.add(Event.class);
    }


    public Set<Class<? extends Event>> getAvailableEvents() {
        return Set.copyOf(this.availableEvents);
    }

    public void addEventClass(Class<? extends Event> cls) {
        /* no need to add root of all events */
        if (cls == Event.class) return;
        this.availableEvents.add(cls);
    }

    public EventTypeNode<Event> compileEventHierarchy() {
        Map<Class<? extends Event>, Set<Class<? extends Event>>> subclasses = new HashMap<>();

        /* first create a map of classes and their immediate super classes */
        for (Class<? extends Event> eventClass : this.availableEvents) {
            Class<? extends Event> superclass = (Class<? extends Event>) eventClass.getSuperclass();
            subclasses.computeIfAbsent(superclass, k -> new HashSet<>()).add(eventClass);
        }

        return buildNode(Event.class, subclasses);
    }

    private <T extends Event> EventTypeNode<T> buildNode(Class<T> currentClass,
                                                         Map<Class<? extends Event>, Set<Class<? extends Event>>> subclassMap) {
        Set<Class<? extends Event>> subclasses = subclassMap.getOrDefault(currentClass, Collections.emptySet());
        Set<EventTypeNode<? extends T>> subTypes = new HashSet<>();
        for (Class<?> subclass : subclasses) {
            subTypes.add(buildNode((Class<? extends T>) subclass, subclassMap));
        }

        return new EventTypeNode<>(subTypes, currentClass);
    }
}
