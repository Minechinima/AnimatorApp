package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

/**
 * Triggered when a node (the triggering node) is removed from the tree.
 */
public class RemoveFromTreeEvent extends Event {
    public RemoveFromTreeEvent(BaseStructure trigger) {
        super(trigger);
    }
}
