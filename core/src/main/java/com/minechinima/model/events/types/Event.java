package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

public abstract class Event {
    private final BaseStructure trigger;

    public Event(BaseStructure trigger) {
        this.trigger = trigger;
    }

    public BaseStructure getTrigger() {
        return this.trigger;
    }
}
