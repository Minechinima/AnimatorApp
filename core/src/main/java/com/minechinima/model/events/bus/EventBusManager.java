package com.minechinima.model.events.bus;

import com.minechinima.model.base.BaseHierarchyRoot;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.EventTypeNode;
import com.minechinima.model.events.EventTypeRegistry;
import com.minechinima.model.events.types.Event;
import com.minechinima.model.memory.BaseStructureReferences;
import com.minechinima.model.memory.ReferenceManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * This manages all available event buses for a data tree and their subscribers.
 */
public class EventBusManager extends BaseStructureReferences {
    /* keep the mappings as simple as possible to easily clear references */
    /**
     * Maps subscribers and their event type to the event bus.
     */
    private final Map<EventBus, Map<Object, Set<EventMethodHandler>>> subscribers = new IdentityHashMap<>();
    /**
     * Cache the references that should be removed
     */
    private final Map<EventBus, Map<Object, Set<EventMethodHandler>>> cachedRemoval = new IdentityHashMap<>();

    private final EventTypeNode<Event> rootType;
    private final Set<Class<? extends Event>> availableEventTypes;
    /**
     * EventBusManager is part of ReferenceManagement, which can only manage references to one data tree.
     * Therefore, we should also check here if eventbus is correct.
     */
    private final BaseHierarchyRoot dataTree;

    public EventBusManager(ReferenceManager referenceManager, BaseHierarchyRoot root, EventTypeRegistry registry) {
        super(referenceManager);
        this.dataTree = root;
        this.rootType = registry.compileEventHierarchy();
        this.availableEventTypes = Set.copyOf(registry.getAvailableEvents());
    }

    /**
     * Register an event bus so others can listen to it.
     * @param bus
     * @param owner the owner of the eventbus.
     * @throws IllegalArgumentException if the owner does not match the owner of the EventBus.
     */
    protected final void registerEventBus(BaseStructure owner, EventBus bus) {
        if (this.subscribers.containsKey(bus)) return;

        /* Passing the owner explicitly makes it clearer that this needs to be part of reference management. */
        if (owner != bus.getOwner()) {
            throw new IllegalArgumentException("EventBus owner mismatch.");
        }

        if (!owner.isInSameTree(this.dataTree)) {
            throw new IllegalArgumentException("Cannot register an EventBus that is not in the data tree.");
        }

        this.subscribers.put(bus, new IdentityHashMap<>());
        bus.addManager(this);
        this.afterAddReference(bus.getOwner());
    }

    /**
     * Subscribe an object to an EventBus. The Object must declare methods with annotations that want to receive events.
     * @param subscriber
     */
    public void subscribe(EventBus bus, Object subscriber) {
        Map<Object, Set<EventMethodHandler>> handlers;
        if ((handlers = this.subscribers.get(bus)) == null) {
            this.registerEventBus(bus.getOwner(), bus);
            handlers = this.subscribers.get(bus);
        }

        /* overwrite old mappings */
        handlers.remove(subscriber);

        Set<EventMethodHandler> methodHandlers = new HashSet<>();
        /* go through the class and all its superclasses, collecting all annotated methods and check them */
        Class clazz = subscriber.getClass();
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);

                if (annotation != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length != 1) {
                        throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but requires " +
                                        parameterTypes.length + " arguments.  Event handler methods must require a single argument.");
                    }

                    if (!Event.class.isAssignableFrom(parameterTypes[0])) {
                        throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, " +
                                "but the method argument is not a subclass or the same as Event.class");
                    }

                    Class<Event> eventType = (Class<Event>) parameterTypes[0];
                    SubscribeEvent.Type listenType = annotation.type();

                    Set<Class<? extends Event>> eventsListened = this.rootType.collectSubEventTypesOf(eventType);

                    if (eventsListened.isEmpty() || !this.availableEventTypes.containsAll(eventsListened)) {
                        throw new IllegalStateException("Method " + method + " tries to listen to an event type that was not registered.");
                    }

                    methodHandlers.add(new EventMethodHandler(method, listenType, eventsListened));
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (!methodHandlers.isEmpty()) {
            handlers.put(subscriber, methodHandlers);
            if (subscriber instanceof BaseStructure referencedNode) {
                this.afterAddReference(referencedNode);
            }
        }
    }

    /**
     *
     * @param event
     * @implNote Package-private so only {@link EventBus} can call.
     */
    final void dispatchEvent(EventBus bus, EventOrigin origin, Event event) {
        if (!this.availableEventTypes.contains(event.getClass())) {
            throw new IllegalArgumentException("The dispatched Event class is not registered.");
        }

        if (!bus.getOwner().isInSameTree(this.dataTree)) {
            throw new IllegalArgumentException("Cannot dispatch an event of a bus from a different tree.");
        }

        Map<Object, Set<EventMethodHandler>> subscribers;
        if ((subscribers = this.subscribers.get(bus)) == null) return;

        for (Map.Entry<Object, Set<EventMethodHandler>> subscriber : subscribers.entrySet()) {
            subscriber.getValue().forEach(eventMethodHandler -> {
                eventMethodHandler.handleEvent(subscriber.getKey(), origin, event);
            });
        }
    }

    @Override
    protected void prepareClearAllReferences() {
        this.cachedRemoval.putAll(this.subscribers);
        this.subscribers.clear();
    }

    @Override
    protected void prepareClearReferences(BaseStructure reference) {
        for (Map.Entry<EventBus, Map<Object, Set<EventMethodHandler>>> entry : this.subscribers.entrySet()) {
            final EventBus removeEventBus = entry.getKey();
            final Map<Object, Set<EventMethodHandler>> removeSubscribers = entry.getValue();

            if (removeEventBus.getOwner() == reference) {
                this.cachedRemoval.put(removeEventBus, removeSubscribers);
                this.subscribers.remove(removeEventBus);
            } else if (removeSubscribers.containsKey(reference)) {
                Map<Object, Set<EventMethodHandler>> subscriber = new HashMap<>();
                subscriber.put(reference, removeSubscribers.get(reference));

                removeSubscribers.remove(reference);
                this.cachedRemoval.put(removeEventBus, subscriber);
            }
        }
    }

    @Override
    protected void finalizeClearReferences() {
        this.cachedRemoval.clear();
    }

    @Override
    protected void undoClearReferences() {
        for (Map.Entry<EventBus, Map<Object, Set<EventMethodHandler>>> entry : this.cachedRemoval.entrySet()) {
            final EventBus removedEventBus = entry.getKey();
            final Map<Object, Set<EventMethodHandler>> removedSubscribers = entry.getValue();

            Map<Object, Set<EventMethodHandler>> storedSubscribers;

            if ((storedSubscribers = this.subscribers.get(removedEventBus)) == null) {
                this.subscribers.put(removedEventBus, storedSubscribers = new IdentityHashMap<>());
            }

            storedSubscribers.putAll(removedSubscribers);
        }

        this.cachedRemoval.clear();
    }

    @Override
    public boolean containsReference(BaseStructure reference) {
        for (Map.Entry<EventBus, Map<Object, Set<EventMethodHandler>>> bus : this.subscribers.entrySet()) {
            if (bus.getKey().getOwner() == reference) return true;
            if (bus.getValue().containsKey(reference)) return true;
        }

        for (Map.Entry<EventBus, Map<Object, Set<EventMethodHandler>>> bus : this.cachedRemoval.entrySet()) {
            if (bus.getKey().getOwner() == reference) return true;
            if (bus.getValue().containsKey(reference)) return true;
        }

        return false;
    }

    private class EventMethodHandler {
        private final Method method;
        private final SubscribeEvent.Type listenType;
        private final Set<Class<? extends Event>> eventTypes = new HashSet<>();

        private EventMethodHandler(Method method, SubscribeEvent.Type listenType, Set<Class<? extends Event>> events) {
            this.listenType = listenType;
            this.method = method;
            this.eventTypes.addAll(events);
            method.setAccessible(true);
        }

        public void handleEvent(Object subscriber, EventOrigin origin, Event event) {
            switch (this.listenType) {
                case ALL:
                    break;
                case ON_NODE:
                    if (origin == EventOrigin.CHILDREN) return;
                    break;
                case IN_CHILDREN:
                    if (origin == EventOrigin.NODE) return;
                    break;
            }

            if (!this.eventTypes.contains(event.getClass())) return;

            try {
                this.method.invoke(subscriber, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                //TODO surround any Exception thrown by runtimeexception
                throw new RuntimeException(e);
            }
        }
    }
}
