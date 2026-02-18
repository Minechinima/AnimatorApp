package com.minechinima.model.events;

import com.minechinima.model.events.types.*;
import com.minechinima.model.events.types.changes.*;

import java.util.*;

@Deprecated(forRemoval = true)
public class EventTypeRegistry {
    private final Set<Class<? extends AbstractEvent>> availableEvents = new HashSet<>();

    public static void registerDefaultEventTypes(EventTypeRegistry registry) {
        registry.availableEvents.add(AbstractEvent.class);
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


    public Set<Class<? extends AbstractEvent>> getAvailableEvents() {
        return Set.copyOf(this.availableEvents);
    }

    public void addEventClass(Class<? extends AbstractEvent> cls) {
        /* no need to add root of all events */
        if (cls == AbstractEvent.class) return;
        this.availableEvents.add(cls);
    }

    public EventTypeNode<AbstractEvent> compileEventHierarchy() {
        Map<Class<? extends AbstractEvent>, Set<Class<? extends AbstractEvent>>> subclasses = new HashMap<>();

        /* first create a map of classes and their immediate super classes */
        for (Class<? extends AbstractEvent> eventClass : this.availableEvents) {
            Class<? extends AbstractEvent> superclass = (Class<? extends AbstractEvent>) eventClass.getSuperclass();
            subclasses.computeIfAbsent(superclass, k -> new HashSet<>()).add(eventClass);
        }

        return buildNode(AbstractEvent.class, subclasses);
    }

    private <T extends AbstractEvent> EventTypeNode<T> buildNode(Class<T> currentClass,
                                                                 Map<Class<? extends AbstractEvent>, Set<Class<? extends AbstractEvent>>> subclassMap) {
        Set<Class<? extends AbstractEvent>> subclasses = subclassMap.getOrDefault(currentClass, Collections.emptySet());
        Set<EventTypeNode<? extends T>> subTypes = new HashSet<>();
        for (Class<?> subclass : subclasses) {
            subTypes.add(buildNode((Class<? extends T>) subclass, subclassMap));
        }

        return new EventTypeNode<>(subTypes, currentClass);
    }
}
