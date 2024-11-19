package com.minechinima.model.events.bus;

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
    Type type() default Type.EVENTS_ON_NODE;

    enum Type {
        /**
         * Listen to events triggered on the node and events triggered in children.
         */
        ALL,
        /**
         * Listen only to events triggered in the children
         */
        EVENTS_IN_CHILDREN,
        /**
         * Listen to events triggered on the node.
         */
        EVENTS_ON_NODE
    }
}