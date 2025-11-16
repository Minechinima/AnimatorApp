package com.minechinima.model.app.object;


import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.ui.utils.ILangKeyProvider;

public abstract class ObjectType extends ValueGroup implements ILangKeyProvider {
    public ObjectType() {
        super("");
    }

    public abstract String getOutlinerIconUnicode();
}
