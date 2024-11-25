package com.minechinima.model.events.bus;

public enum EventOrigin {
    /**
     * Event got triggered in the children and now bubbles up.
     */
    CHILDREN,
    /**
     * Event got triggered on the node itself
     */
    NODE
}
