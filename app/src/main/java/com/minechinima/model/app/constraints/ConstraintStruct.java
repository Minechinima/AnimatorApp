package com.minechinima.model.app.constraints;

import com.minechinima.Application;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.ValuePointer;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.utils.ILangKeyProvider;

public abstract class ConstraintStruct extends ValueGroup implements ILangKeyProvider {
    public ValuePointer<BaseObject> target = new ValuePointer<>("target", BaseObject.class, Application.NAMESPACE);

    public ConstraintStruct(String id) {
        super(id);
        this.addChild("target", target);
    }
}
