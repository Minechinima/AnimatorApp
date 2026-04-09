package com.minechinima.model.proxy.values;

/**
 * Annotation for generic proxy classes that need to know their generic type.
 */
public @interface ValueType {
    Class<? extends Object> value();
}
