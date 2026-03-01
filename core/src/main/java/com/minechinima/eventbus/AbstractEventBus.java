package com.minechinima.eventbus;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * General-purpose abstract event bus that handles annotation-based subscriber registration and
 * event dispatching.
 * <br><br>
 * To create an event bus for a specific domain:
 * <ol>
 *   <li>Define a base event class (E) for your domain.</li>
 *   <li>Define a constraint context class (C) that carries dispatch-time information used to filter
 *       listeners (e.g., whether the event originated on the node itself or in a child node).</li>
 *   <li>Define an annotation (A) that listener methods are annotated with. The annotation can
 *       declare constraint fields – for IDE auto-suggest, define a base constraint class that
 *       extends {@link IEventConstraint}&lt;C&gt; and use {@code Class<? extends YourBaseConstraint>}
 *       as an annotation element type.</li>
 *   <li>Extend this class and implement {@link #evaluateConstraints(Annotation, Object)} to
 *       determine whether a listener with the given annotation should receive the event.</li>
 * </ol>
 *
 * Example:
 * <pre>{@code
 * class MyEventBus extends AbstractEventBus<MyBaseEvent, MyContext, MyListener> {
 *     protected MyEventBus() {
 *         super(MyBaseEvent.class, MyListener.class);
 *     }
 *
 *     @Override
 *     protected boolean evaluateConstraints(MyListener annotation, MyContext context) {
 *         for (Class<? extends MyConstraint> c : annotation.constraints()) {
 *             if (!c.getDeclaredConstructor().newInstance().matches(context)) return false;
 *         }
 *         return true;
 *     }
 * }
 * }</pre>
 *
 * @param <E> the base type of all events dispatched through this bus
 * @param <C> the constraint context type supplied at dispatch time
 * @param <A> the annotation type that marks event listener methods
 */
public abstract class AbstractEventBus<E, C, A extends Annotation> {

    /**
     * Maps each subscriber object to the set of handler descriptors discovered on that object.
     * Uses an {@link IdentityHashMap} so that the same object registered multiple times is
     * treated as a single subscriber entry.
     */
    private final Map<Object, Set<MethodHandler>> subscribers = new IdentityHashMap<>();

    private final Class<E> baseEventType;
    private final Class<A> annotationType;

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
     * Remove a subscriber from this event bus.  If the object is not subscribed, this is a no-op.
     *
     * @param subscriber the listener object to deregister
     */
    public void unsubscribe(Object subscriber) {
        this.subscribers.remove(subscriber);
    }

    /**
     * Dispatch an event to all registered listeners that satisfy the constraints encoded in their
     * annotation for the supplied constraint context.
     * <br><br>
     * Each listener method is only invoked when:
     * <ul>
     *   <li>{@link #evaluateConstraints(Annotation, Object)} returns {@code true} for the
     *       listener's annotation and the given context, and</li>
     *   <li>the listener's declared parameter type is assignable from the runtime type of
     *       {@code event}.</li>
     * </ul>
     *
     * @param event             the event to dispatch
     * @param constraintContext the context to pass to {@link #evaluateConstraints}
     */
    protected final void dispatch(E event, C constraintContext) {
        this.subscribers.forEach((subscriber, handlers) ->
                handlers.forEach(handler -> handler.handle(subscriber, event, constraintContext)));
    }

    /**
     * Determine whether a listener method annotated with {@code annotation} should receive an
     * event that was dispatched with the given {@code constraintContext}.
     * <br><br>
     * Implementations use the fields of the annotation (which encode the developer's constraints)
     * together with the dispatch-time context to make this decision.
     * <br><br>
     * When the listener annotation exposes an array of {@link IEventConstraint} class objects,
     * implementations can delegate to {@link #evaluateConstraintClasses} to avoid repeating the
     * instantiation and evaluation loop.
     *
     * @param annotation        the annotation found on the listener method
     * @param constraintContext the context provided at dispatch time
     * @return {@code true} if the listener should receive the event
     */
    protected abstract boolean evaluateConstraints(A annotation, C constraintContext);

    /**
     * Utility method for implementations whose listener annotations declare an array of
     * {@link IEventConstraint} class objects.  Every class is instantiated via its no-arg
     * constructor and evaluated against {@code constraintContext}; <em>all</em> must return
     * {@code true} for this method to return {@code true} (logical AND).
     * <br><br>
     * Because listener annotations typically expose a domain-specific base constraint type
     * (e.g. {@code Class<? extends MyConstraint>[]}) rather than
     * {@code Class<? extends IEventConstraint<C>>[]}, callers usually need an unchecked cast
     * at the call site.  That cast is safe as long as the concrete constraint classes implement
     * {@link IEventConstraint}&lt;C&gt;, which is guaranteed by the annotation's element type.
     * <br><br>
     * Example usage inside {@code evaluateConstraints}:
     * <pre>{@code
     * @Override
     * protected boolean evaluateConstraints(MyListener annotation, MyContext ctx) {
     *     @SuppressWarnings("unchecked")
     *     Class<? extends IEventConstraint<MyContext>>[] constraints =
     *             (Class<? extends IEventConstraint<MyContext>>[]) annotation.constraints();
     *     return evaluateConstraintClasses(constraints, ctx);
     * }
     * }</pre>
     *
     * @param constraintClasses the constraint classes declared in the listener annotation
     * @param constraintContext the context to evaluate each constraint against
     * @return {@code true} if every constraint matches (or the array is empty)
     * @throws RuntimeException wrapping any {@link ReflectiveOperationException} if a constraint
     *                          class cannot be instantiated via its no-arg constructor
     */
    protected final boolean evaluateConstraintClasses(
            Class<? extends IEventConstraint<C>>[] constraintClasses, C constraintContext) {
        for (Class<? extends IEventConstraint<C>> cls : constraintClasses) {
            try {
                if (!cls.getDeclaredConstructor().newInstance().matches(constraintContext)) {
                    return false;
                }
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Failed to instantiate event constraint class: " + cls.getName(), e);
            }
        }
        return true;
    }

    /**
     * Descriptor for a single annotated listener method, held inside {@link #subscribers}.
     */
    private class MethodHandler {
        private final Method method;
        private final A annotation;
        private final Class<? extends E> eventType;

        private MethodHandler(Method method, A annotation, Class<? extends E> eventType) {
            this.method = method;
            this.annotation = annotation;
            this.eventType = eventType;
            method.setAccessible(true);
        }

        public void handle(Object subscriber, E event, C constraintContext) {
            if (!evaluateConstraints(this.annotation, constraintContext)) return;
            if (!this.eventType.isAssignableFrom(event.getClass())) return;

            try {
                this.method.invoke(subscriber, event);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
