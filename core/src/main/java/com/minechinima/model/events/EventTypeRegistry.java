package com.minechinima.model.events;

import com.minechinima.model.events.types.AddToTreeEvent;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.events.types.ParentChangeEvent;
import com.minechinima.model.events.types.RemoveFromTreeEvent;
import com.minechinima.model.events.types.changes.*;

import java.util.*;

@Deprecated(forRemoval = true)
public class EventTypeRegistry {
    private final Set<Class<? extends Event>> availableEvents = new HashSet<>();

    public static void registerDefaultEventTypes(EventTypeRegistry registry) {
        registry.availableEvents.add(Event.class);
        /* register the module's default event types from type package */
        registry.availableEvents.add(RemoveFromTreeEvent.class);
        registry.availableEvents.add(AddToTreeEvent.class);
        registry.availableEvents.add(ParentChangeEvent.class);

        /* change events */
        registry.availableEvents.add(ChangeEvent.class);

        registry.availableEvents.add(IdentifierChangeEvent.class);
        registry.availableEvents.add(ValueChangeEvent.class);

        registry.availableEvents.add(ListAddEvent.class);
        registry.availableEvents.add(ListIndexChangeEvent.class);
        registry.availableEvents.add(ListRemoveEvent.class);

        registry.availableEvents.add(ValueRemoveEvent.class);
        registry.availableEvents.add(ValueAddEvent.class);
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
