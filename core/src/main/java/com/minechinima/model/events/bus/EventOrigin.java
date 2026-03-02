package com.minechinima.model.events.bus;

import com.minechinima.eventbus.IEventContext;

public enum EventOrigin implements IEventContext {
    /**
     * Event got triggered in the children and now bubbles up.
     */
    CHILDREN,
    /**
     * Event got triggered on the node itself
     */
    NODE
}
