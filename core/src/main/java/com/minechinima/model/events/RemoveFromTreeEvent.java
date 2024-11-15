package com.minechinima.model.events;

import com.minechinima.model.base.BaseStructure;

public class RemoveFromTreeEvent extends Event {
    public RemoveFromTreeEvent(BaseStructure trigger) {
        super(trigger);
    }
}
