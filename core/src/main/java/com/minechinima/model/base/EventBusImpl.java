package com.minechinima.model.base;

import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.types.Event;

/**
 * Implementation of EventBus for the data tree, so the only possible way
 * to dispatch an event is in a BaseStructure.
 * The dispatch methods are therefore package-private
 */
public class EventBusImpl extends EventBus {
    public EventBusImpl(BaseStructure owner) {
        super(owner);
    }

    final void _dispatchEventOnThis(Event event) {
        super.dispatchEventOnThis(event);
    }

    final void _dispatchEventFromChildNode(Event event) {
        super.dispatchEventFromChildNode(event);
    }
}
