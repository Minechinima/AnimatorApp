package com.minechinima.model.events.changes;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.IDataOperation;
import com.minechinima.model.events.Event;

/**
 * This is a change event when the value tree state changes in some way that can be undone.
 * @param <T> type of the triggering Element
 */
public abstract class ChangeEvent<T extends IDataOperation> extends Event {
    private final T changeOperation;

    public ChangeEvent(BaseStructure trigger, T changeOperation) {
        super(trigger);
        this.changeOperation = changeOperation;
    }

    public T getDataOperation() {
        return this.changeOperation;
    }
}
