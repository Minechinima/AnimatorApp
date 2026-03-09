package com.minechinima.model.events.bus;

import com.minechinima.eventbus.IEventConstraint;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for a method that wants to listen to events from the data tree.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface SubscribeEvent {
    Type type() default Type.ON_NODE;

    enum Type implements IEventConstraint<EventOrigin> {
        /**
         * Listen to events triggered on the node and events triggered in children.
         */
        ALL,
        /**
         * Listen only to events triggered in the children
         */
        IN_CHILDREN,
        /**
         * Listen to events triggered on the node.
         */
        ON_NODE;

        @Override
        public boolean matches(EventOrigin constraintContext) {
            return switch (this) {
                case ALL -> true;
                case IN_CHILDREN -> constraintContext == EventOrigin.CHILDREN;
                case ON_NODE -> constraintContext == EventOrigin.NODE;
            };
        }
    }
}