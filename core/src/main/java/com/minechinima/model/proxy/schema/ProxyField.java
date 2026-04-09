package com.minechinima.model.proxy.schema;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a proxy field as part of the schema tree declaration.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ProxyField {
    /**
     * Explicit schema key. Empty means field name is used.
     */
    String key() default "";
}

