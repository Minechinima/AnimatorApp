package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;

/**
 * This event is fired when a node (the triggering node) is added to the tree for the first time.<br>
 * The event is <b>not</b> fired when a node is moved inside the tree i.e. parent changes.
 */
public class AddToTreeEvent extends Event {
    public AddToTreeEvent(BaseStructure trigger) {
        super(trigger);
    }
}
