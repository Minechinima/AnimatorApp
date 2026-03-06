package com.minechinima.eventbus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * General purpose EventBus that supports annotation-based subscriber registration.
 * <br><br>
 * To create an event bus for a specific domain:
 *
 * <ol>
 *   <li>Define a base event class (E) for your domain.</li>
 *   <li>Define an annotation (A) that listener methods are annotated with.</li>
 * </ol>
 *
 * @param <E>
 * @param <A>
 */
public class AbstractEventBus<E, A extends Annotation> {

    /**
     * Maps each subscriber object to the set of handler descriptors discovered on that object.
     * Uses an {@link IdentityHashMap} so that the same object registered multiple times is
     * treated as a single subscriber entry.
     */
    protected final Map<Object, Set<MethodHandler>> subscribers = new IdentityHashMap<>();

    protected final Class<E> baseEventType;
    protected final Class<A> annotationType;

    /**
     * @param baseEventType  the class object for the base event type E; used to validate that
     *                       listener method parameters are compatible
     * @param annotationType the class object for the listener annotation A; used to look up
     *                       annotated methods via reflection
     */
    protected AbstractEventBus(Class<E> baseEventType, Class<A> annotationType) {
        this.baseEventType = Objects.requireNonNull(baseEventType, "baseEventType must not be null");
        this.annotationType = Objects.requireNonNull(annotationType, "annotationType must not be null");
    }

    /**
     * Subscribe an object to this event bus.  The object's class hierarchy is scanned for methods
     * annotated with the annotation type A.  Each annotated method must declare exactly one
     * parameter whose type is the base event type E or a subtype of it.
     * <br><br>
     * Calling this method on an already-subscribed object replaces the existing subscription.
     *
     * @param subscriber the listener object to register
     * @throws IllegalArgumentException if an annotated method does not declare exactly one
     *                                  parameter, or if that parameter is not a subtype of E
     */
    public void subscribe(Object subscriber) {
        /* overwrite old mappings */
        this.subscribers.remove(subscriber);

        Set<MethodHandler> handlers = new HashSet<>();
        /* traverse the class hierarchy so methods declared in superclasses are also discovered */
        Class<?> clazz = subscriber.getClass();
        while (clazz != null) {
            for (Method method : clazz.getDeclaredMethods()) {
                A annotation = method.getAnnotation(this.annotationType);
                if (annotation == null) continue;

                Class<?>[] paramTypes = method.getParameterTypes();

                if (paramTypes.length != 1) {
                    throw new IllegalArgumentException("Method " + method + " has a listener annotation, but declares "
                            + paramTypes.length + " parameter(s). Event listener methods must declare exactly one parameter.");
                }

                if (!this.baseEventType.isAssignableFrom(paramTypes[0])) {
                    throw new IllegalArgumentException("Method " + method + " has a listener annotation, "
                            + "but its parameter type is not a subtype of " + this.baseEventType.getSimpleName() + ".");
                }

                @SuppressWarnings("unchecked")
                Class<? extends E> eventType = (Class<? extends E>) paramTypes[0];
                handlers.add(new MethodHandler(method, annotation, eventType));
            }
            clazz = clazz.getSuperclass();
        }

        if (!handlers.isEmpty()) {
            this.subscribers.put(subscriber, handlers);
        }
    }

    /**
     * Remove a subscriber from this event bus.
     *
     * @param subscriber the listener object to deregister
     */
    public void unsubscribe(Object subscriber) {
        this.subscribers.remove(subscriber);
    }

    /**
     * Dispatch an event to all registered listeners that listen to the event or event superclass.
     * @param event the event to dispatch
     */
    protected final void dispatch(E event) {
        this.subscribers.forEach((subscriber, handlers) ->
                handlers.forEach(handler -> handler.handle(subscriber, event)));
    }

    /**
     * Descriptor for a single annotated listener method, held inside {@link #subscribers}.
     */
    protected class MethodHandler {
        protected final Method method;
        protected final A annotation;
        protected final Class<? extends E> eventType;

        private MethodHandler(Method method, A annotation, Class<? extends E> eventType) {
            this.method = method;
            this.annotation = annotation;
            this.eventType = eventType;
            method.setAccessible(true);
        }

        public void handle(Object subscriber, E event) {
            if (!this.eventType.isAssignableFrom(event.getClass())) return;

            try {
                this.method.invoke(subscriber, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
