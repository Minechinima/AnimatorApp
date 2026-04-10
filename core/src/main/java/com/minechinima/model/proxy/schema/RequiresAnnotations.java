package com.minechinima.model.proxy.schema;

import java.lang.annotation.*;

/**
 * Annotate a proxy class with this to declare that an attribute, type of the proxy class that has this annotation,
 * requires more annotations in the field. Can be used to request explicit factory annotations to instantiate generic types.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface RequiresAnnotations {
    Class<? extends Annotation>[] annotations();
}
