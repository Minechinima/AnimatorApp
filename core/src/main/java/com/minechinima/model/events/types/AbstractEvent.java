package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

public sealed abstract class AbstractEvent permits ChangeEvent, Event {
    private final BaseStructure trigger;

    public AbstractEvent(BaseStructure trigger) {
        this.trigger = trigger;
    }

    public BaseStructure getTrigger() {
        return this.trigger;
    }
}
