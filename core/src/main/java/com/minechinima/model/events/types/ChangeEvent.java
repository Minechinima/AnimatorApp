package com.minechinima.model.events.types;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.operations.IDataOperation;

/**
 * This is a change event when the value tree state changes in some way that can be undone.
 * @param <T> type of the triggering Element
 */
public abstract non-sealed class ChangeEvent<T extends IDataOperation> extends AbstractEvent {
    private final T changeOperation;

    public ChangeEvent(BaseStructure trigger, T changeOperation) {
        super(trigger);
        this.changeOperation = changeOperation;
    }

    public T getDataOperation() {
        return this.changeOperation;
    }
}
