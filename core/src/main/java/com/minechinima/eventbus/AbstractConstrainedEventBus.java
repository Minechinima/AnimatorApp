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
 *   <li>Define an annotation (A) that listener methods are annotated with.
 *       The annotation can provide constraints as annotation values.
 *       Every constraint needs to extend {@link IEventConstraint} and use the current type of {@link IEventContext}.
 *       The available constraint implementations need to be registered in the constructor of the event bus implementation.
 *       </li>
 * </ol>
 *
 * Example:
 * <pre>{@code
 * class MyContext implements IEventContext {
 *      private final Channel channel;
 *
 *      public enum Channel {
 *          A, B, C;
 *      }
 * }
 *
 * public @interface MyListener {
 *      ChannelConstraint channel() default ChannelConstraint.ALL;
 *
 *      public enum ChannelConstraint implements IEventConstraint<MyContext> {
 *          A, B, C, ALL;
 *
 *         @Override
 *         public boolean matches(MyContext context) {
 *              return switch(this) {
 *                  case ALL -> true;
 *                  case A -> context.getChannel() == Channel.A;
 *                  case B -> context.getChannel() == Channel.B;
 *                  case C -> context.getChannel() == Channel.C;
 *              };
 *         }
 *      }
 * }
 *
 * class MyEventBus extends AbstractConstrainedEventBus<MyBaseEvent, MyContext, MyListener> {
 *     protected MyEventBus() {
 *         super(MyBaseEvent.class, MyListener.class, MyListener.ChannelConstraint.class);
 *     }
 * }
 * }</pre>
 *
 * @param <E> the base type of all events dispatched through this bus
 * @param <C> the constraint context type supplied at dispatch time
 * @param <A> the annotation type that marks event listener methods
 */
public abstract class AbstractConstrainedEventBus<E, C extends IEventContext, A extends Annotation> extends AbstractEventBus<E, A> {

    /**
     * Set of constraints to use when evaluating listener annotation fields.
     */
    private final Set<Class<? extends IEventConstraint<C>>> constraints = new HashSet<>();

    /**
     * @param baseEventType  the class object for the base event type E; used to validate that
     *                       listener method parameters are compatible
     * @param annotationType the class object for the listener annotation A; used to look up
     *                       annotated methods via reflection
     */
    protected AbstractConstrainedEventBus(Class<E> baseEventType, Class<A> annotationType, Class<? extends IEventConstraint<C>>... constraints) {
        super(baseEventType, annotationType);
        this.constraints.addAll(List.of(constraints));
    }

    /**
     * Dispatch an event to all registered listeners that satisfy the constraints encoded in their
     * annotation for the supplied constraint context.
     * <br><br>
     * Each listener method is only invoked when:
     * <ul>
     *   <li>{@link #evaluateConstraints(Annotation, IEventContext)} returns {@code true} for the
     *       listener's annotation and the given context, and</li>
     *   <li>the listener's declared parameter type is assignable from the runtime type of
     *       {@code event}.</li>
     * </ul>
     *
     * @param event             the event to dispatch
     * @param constraintContext the context to pass to {@link #evaluateConstraints}
     */
    protected final void dispatch(E event, C constraintContext) {
        this.subscribers.forEach((subscriber, handlers) -> {
            handlers.forEach(handler -> {
                if (!evaluateConstraints(handler.annotation, constraintContext)) return;
                handler.handle(subscriber, event);
            });
        });
    }

    /**
     * Determine whether a listener method annotated with {@code annotation} should receive an
     * event that was dispatched with the given {@code constraintContext}.
     * <br><br>
     * Implementations use the fields of the annotation (which encode the developer's constraints)
     * together with the dispatch-time context to make this decision.
     * <br><br>
     * When the listener annotation exposes an array of {@link IEventConstraint} class objects,
     * implementations can delegate to {@link #evaluateConstraints} to avoid repeating the
     * instantiation and evaluation loop.
     *
     * @param annotation        the annotation found on the listener method
     * @param constraintContext the context provided at dispatch time
     * @return {@code true} if the listener should receive the event
     */
    protected boolean evaluateConstraints(A annotation, C constraintContext) {
        for (IEventConstraint<C> c : this.extractConstraintsFromAnnotation(annotation)) {
            if (!c.matches(constraintContext)) return false;
        }
        return true;
    }

    private Set<IEventConstraint<C>> extractConstraintsFromAnnotation(A annotation) {
        Set<IEventConstraint<C>> foundConstraints = new HashSet<>();
        for (Method method : this.annotationType.getDeclaredMethods()) {
            Class<?> returnType = method.getReturnType();
            boolean isArray = returnType.isArray();
            Class<?> componentType = isArray ? returnType.getComponentType() : returnType;

            /* check if the return type of the annotation value is assignable to a registered constraint class
            * - ensures generic type matches */
            if (this.isConstraintClassRegistered(componentType)) {
                try {
                    Object value = method.invoke(annotation);
                    if (value == null) continue;

                    if (isArray) {
                        for (Object v : (Object[]) value) {
                            if (this.isConstraintClassRegistered(v.getClass())) {
                                foundConstraints.add((IEventConstraint<C>) v);
                            }
                        }
                    } else {
                        foundConstraints.add((IEventConstraint<C>) value);
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Failed to instantiate constraint from annotation", e);
                }
            }
        }

        return foundConstraints;
    }

    protected boolean isConstraintClassRegistered(Class<?> constraintClass) {
        for (Class<? extends IEventConstraint<C>> c : this.constraints) {
            if (c.isAssignableFrom(constraintClass)) return true;
        }
        return false;
    }
}
