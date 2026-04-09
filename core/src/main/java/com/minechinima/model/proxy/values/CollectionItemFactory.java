package com.minechinima.model.proxy.values;

import com.minechinima.model.proxy.schema.AnnotationExpand;

/**
 * Marks what type the items of a generic collections are.
 */
public @interface CollectionItemFactory {
    /**
     * @return the proxy class
     */
    AnnotationExpand value();
}
