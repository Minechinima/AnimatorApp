package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

public abstract non-sealed class Event extends AbstractEvent {
    public Event(BaseStructure trigger) {
        super(trigger);
    }
}
