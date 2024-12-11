package com.minechinima.model.app.object;


import com.minechinima.model.values.base.ValueGroup;

public abstract class ObjectType extends ValueGroup {
    public ObjectType() {
        super("");
    }

    public abstract String getOutlinerIconUnicode();
}
