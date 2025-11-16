package com.minechinima.operator.ot;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.ot.commands.IPropertyOperator;

public abstract class OperatorTypePropertyProvider<P extends OperatorPropertiesGroupStruct,
                                                   O extends IPropertyOperator<C, P>,
                                                   C extends IContext>
        extends OperatorType<C, O> implements IOTPropertyProvider<O, P> {
    private final OTPropertyStorage<P> storage = new OTPropertyStorage<>("", this::createProps);

    public OperatorTypePropertyProvider(String id, String title, String description, boolean pushUndo) {
        super(id, title, description, pushUndo);
        this.addChild("storage", this.storage);
        this.storage.initChildren();
    }

    public OperatorTypePropertyProvider(String id, String title, String description) {
        this(id, title, description, false);
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
