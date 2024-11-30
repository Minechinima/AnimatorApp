package com.minechinima.model.events;

import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.ParentChangeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.events.types.changes.*;

import java.util.*;

public class EventTypeRegistry {
    private final Set<Class<? extends Event>> availableEvents = new HashSet<>();

    public EventTypeRegistry() {
        this.availableEvents.add(Event.class);
        /* register the module's default event types from type package */
        this.availableEvents.add(RemoveFromTreeEvent.class);
        this.availableEvents.add(AddToTreeEvent.class);
        this.availableEvents.add(ParentChangeEvent.class);

        /* change events */
        this.availableEvents.add(ChangeEvent.class);

        this.availableEvents.add(IdentifierChangeEvent.class);
        this.availableEvents.add(ValueChangeEvent.class);

        this.availableEvents.add(ListAddEvent.class);
        this.availableEvents.add(ListIndexChangeEvent.class);
        this.availableEvents.add(ListRemoveEvent.class);

        this.availableEvents.add(ValueRemoveEvent.class);
        this.availableEvents.add(ValueAddEvent.class);
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
