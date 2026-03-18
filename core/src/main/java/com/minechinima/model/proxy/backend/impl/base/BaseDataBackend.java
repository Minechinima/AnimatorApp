package com.minechinima.model.proxy.backend.impl.base;

import com.minechinima.model.api.IDataTree;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.logging.DataOperationBus;
import com.minechinima.model.proxy.backend.api.IDataBackend;

/**
 * base for all backends, as it contains utility methods and default implementations.
 */
public abstract class BaseDataBackend<T extends IDataBackend<T>> implements IDataBackend<T> {
    protected T next;

    @Override
    public void next(T handler) {
        this.next = handler;
    }

    /* default implementations, as pretty much only database backend can implement these meaningfully */


    @Override
    public String getIdentifier() {
        return this.next != null ? this.next.getIdentifier() : null;
    }

    @Override
    public IDataTree getParent() {
        return this.next != null ? this.next.getParent() : null;
    }

    @Override
    public IDataTree getRoot() {
        return this.next != null ? this.next.getRoot() : null;
    }

    @Override
    public boolean isChildOf(IDataTree parent) {
        return this.next != null ? this.next.isChildOf(parent) : false;
    }

    @Override
    public EventBus getEventBus() {
        return this.next != null ? this.next.getEventBus() : null;
    }

    @Override
    public DataOperationBus getDataOperationBus() {
        return this.next != null ? this.next.getDataOperationBus() : null;
    }
}
