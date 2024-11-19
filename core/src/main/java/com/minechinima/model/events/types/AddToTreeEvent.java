package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

/**
 * This event is fired when a node is added to the tree.
 */
public class AddToTreeEvent extends Event {
    public AddToTreeEvent(BaseStructure trigger) {
        super(trigger);
    }
}
