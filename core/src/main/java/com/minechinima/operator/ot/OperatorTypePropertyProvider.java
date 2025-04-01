package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorProperties;

public abstract class OperatorTypePropertyProvider<P extends OperatorProperties, C extends IContext>
        extends OperatorType<C> implements IOTPropertyProvider<P> {
    private final OTPropertyStorage<P> storage = new OTPropertyStorage<>("", this::createProps);

    public OperatorTypePropertyProvider(String id, String title, String description, int flags) {
        super(id, title, description, flags);
        this.addChild("storage", this.storage);
        this.storage.initChildren();
    }

    public OperatorTypePropertyProvider(String id, String title, String description) {
        this(id, title, description, 0);
    }

    public abstract P createProps();

    @Override
    public final void addRuntimeProps(P props) {
        this.storage.addRuntimeProps(props);
    }

    @Override
    public final void removeRuntimeProps(P props) {
        this.storage.removeRuntimeProps(props);
    }
}
