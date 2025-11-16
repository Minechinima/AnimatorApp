package com.minechinima.operator.ot;

import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.UIOperatorContext;
import com.minechinima.operator.ot.commands.IPropertyOperator;

public abstract class UIOperatorTypePropertyProvider<O extends IPropertyOperator<UIOperatorContext, P>,
                                                     P extends OperatorPropertiesGroupStruct>
        extends UIOperatorType<O> implements IOTPropertyProvider<O, P> {
    private final OTPropertyStorage<P> storage = new OTPropertyStorage<>("", this::createProps);

    public UIOperatorTypePropertyProvider(String id, String title, String description, boolean pushUndo) {
        super(id, title, description, pushUndo);
        this.addChild("storage", this.storage);
        this.storage.initChildren();
    }

    public UIOperatorTypePropertyProvider(String id, String title, String description) {
        this(id, title, description, false);
    }

    @Override
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
