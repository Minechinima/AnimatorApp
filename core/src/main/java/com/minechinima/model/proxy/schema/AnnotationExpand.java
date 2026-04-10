package com.minechinima.model.proxy.schema;

import com.minechinima.model.proxy.BaseDataProxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this as an annotation attribute to expand the annotation search to other proxy classes.
 * This is useful for nesting generics and requiring the annotations of the nested generic types.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface AnnotationExpand {
    Class<? extends BaseDataProxy> value();
}
