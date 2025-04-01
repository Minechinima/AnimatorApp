package com.minechinima.model.events.bus;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.types.Event;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * The base EventBus class for a {@link BaseStructure} node to dispatch events through.
 * An EventBus always belongs to one {@link BaseStructure} node.
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
     * Maps subscribers and their event type to the event bus.
     */
    private final Map<Object, Set<EventMethodHandler>> subscribers = new IdentityHashMap<>();
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
     * Subscribe an object to an EventBus. The Object must declare methods with annotations that want to receive events.
     * @param subscriber
     */
    public void subscribe(Object subscriber) {
        /* overwrite old mappings */
        this.subscribers.remove(subscriber);

        Set<EventMethodHandler> methodHandlers = new HashSet<>();
        /* go through the class and all its superclasses, collecting all annotated methods and check them */
        //TODO possible optimization (cache methodhandlers for classes?)
        Class clazz = subscriber.getClass();
        while (clazz != null) {
            /* go through methods and search for subscribe annotation */
            for (Method method : clazz.getDeclaredMethods()) {
                SubscribeEvent annotation = method.getAnnotation(SubscribeEvent.class);

                if (annotation != null) {
                    Class<?>[] parameterTypes = method.getParameterTypes();

                    /* validate subscription method */
                    if (parameterTypes.length != 1) {
                        throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, but declares " +
                                parameterTypes.length + " arguments.  Event handler methods must require a single argument.");
                    }

                    if (!Event.class.isAssignableFrom(parameterTypes[0])) {
                        throw new IllegalArgumentException("Method " + method + " has @SubscribeEvent annotation, " +
                                "but the method argument is not a subclass or the same as Event.class");
                    }

                    Class<Event> eventType = (Class<Event>) parameterTypes[0];
                    SubscribeEvent.Type listenType = annotation.type();

                    methodHandlers.add(new EventMethodHandler(method, listenType, eventType));
                }
            }
            clazz = clazz.getSuperclass();
        }

        if (!methodHandlers.isEmpty()) {
            this.subscribers.put(subscriber, methodHandlers);
        }
    }

    public void unsubscribe(Object subscriber) {
        this.subscribers.remove(subscriber);
    }

    /**
     * Dispatch an event that happened on this node.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventOnThis(Event event) {
        if (event.getTrigger() != this.owner) {
            throw new IllegalArgumentException("Cannot dispatch an event with a different trigger node than the bus owner.");
        }

        this.subscribers.forEach((subscriber, handlers) -> {
            handlers.forEach(handler -> handler.handleEvent(subscriber, EventOrigin.NODE, event));
        });
    }

    /**
     * Dispatch an event that happened in the child nodes of this EventBus owner.
     * @param event
     * @throws IllegalArgumentException if the event trigger is not a child node of this bus owner.
     * @implNote protected so only subclasses can call it and control the access modifier further, to secure what can
     * dispatch events through it.
     */
    protected final void dispatchEventFromChildNode(Event event) {
        if (!(this.owner instanceof BaseValueHierarchy<?> hierarchy) || !event.getTrigger().isChildOf(hierarchy)) {
            throw new IllegalArgumentException("Cannot dispatch a child event from a node that is not child of the bus owner.");
        }

        this.subscribers.forEach((subscriber, handlers) -> {
            handlers.forEach(handler -> handler.handleEvent(subscriber, EventOrigin.CHILDREN, event));
        });
    }

    private static class EventMethodHandler {
        private final Method method;
        private final SubscribeEvent.Type listenType;
        private final Class<? extends Event> eventType;

        private EventMethodHandler(Method method, SubscribeEvent.Type listenType, Class<? extends Event> eventtype) {
            this.listenType = listenType;
            this.method = method;
            this.eventType = eventtype;
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

            //TODO possible optimization (this would be called for all parent nodes when event bubbles up)
            if (!this.eventType.isAssignableFrom(event.getClass())) return;

            try {
                this.method.invoke(subscriber, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
