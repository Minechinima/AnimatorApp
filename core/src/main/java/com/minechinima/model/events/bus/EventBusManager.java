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
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

/**
 * This manages all available event buses for a data tree.
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
    private final BaseHierarchyRoot dataTree;

    public EventBusManager(BaseHierarchyRoot dataTree, ReferenceManager manager, EventTypeRegistry registry) {
        super(manager);
        this.dataTree = dataTree;
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

        if (!owner.isChildOf(this.dataTree) && owner != this.dataTree) {
            throw new IllegalArgumentException("Cannot register an EventBus that is not in the data tree.");
        }

        this.subscribers.put(bus, new IdentityHashMap<>());
        bus.setManager(this);
        this.afterAddReference(bus.getOwner());
    }

    /**
     * Subscribe an object to an EventBus. The Object must declare methods with annotations that want to receive events.
     * @param subscriber
     */
    public final void subscribe(EventBus bus, Object subscriber) {
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
            for (Method method : clazz.getMethods()) {
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

        handlers.put(subscriber, methodHandlers);
    }

    /**
     * Safety feature for {@link EventBus}.
     * @param bus
     * @implNote Package-private so only {@link EventBus} can remove itself. This is for a safety check
     * if the manager in the EventBus changes for some reason.
     */
    final void _removeEventBus(EventBus bus) {
        this.subscribers.remove(bus);
        this.afterRemoveReference(bus.getOwner());
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

        Map<Object, Set<EventMethodHandler>> subscribers;
        if ((subscribers = this.subscribers.get(bus)) == null) return;

        for (Map.Entry<Object, Set<EventMethodHandler>> subscriber : subscribers.entrySet()) {
            subscriber.getValue().forEach(eventMethodHandler -> {
                eventMethodHandler.handleEvent(subscriber.getKey(), origin, event);
            });
        }
    }

    @Override
    protected void prepareClearReferences(BaseStructure reference) {

    }

    @Override
    protected void finalizeClearReferences() {

    }

    @Override
    protected void undoClearReferences() {

    }

    @Override
    public boolean containsReference(BaseStructure reference) {
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
                case EVENTS_ON_NODE:
                    if (origin == EventOrigin.CHILDREN) return;
                    break;
                case EVENTS_IN_CHILDREN:
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
