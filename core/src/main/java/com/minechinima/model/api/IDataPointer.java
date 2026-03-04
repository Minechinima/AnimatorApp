package com.minechinima.model.api;

public interface IDataPointer<T extends IData> extends IData {
    /**
     * Gets the target node that this pointer references.
     *
     * @return the target node, or null if the pointer is not set or the target is invalid
     */
    T get();
}
