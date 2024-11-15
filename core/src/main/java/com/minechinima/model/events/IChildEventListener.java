package com.minechinima.model.events;

/**
 * Observer pattern. This is the Observer.
 */
public interface IChildEventListener<T extends Event> {
    /**
     * Listens to an event of type T happening in the children.
     * @param event
     * @implNote default implementation here to prevent this interface being used as a lambda for method references.<br>
     * You must always pass a class implementing the interface so the memory management code can check if it's a BaseStructure.
     * Otherwise, memory leaks could happen.
     */
    default void onChildEvent(T event) {
        throw new IllegalStateException("Not implemented");
    }
}
