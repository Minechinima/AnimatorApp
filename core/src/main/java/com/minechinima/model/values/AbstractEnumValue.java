package com.minechinima.model.values;

import com.minechinima.model.values.base.BaseValue;
import com.minechinima.ui.utils.IUIEnumValue;
import com.minechinima.ui.utils.ILangKeyProvider;

public abstract class AbstractEnumValue<T extends ILangKeyProvider> extends BaseValue<T> implements IUIEnumValue<T, BaseValue<T>> {
    public AbstractEnumValue(String id, T defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public T getCurrentValue() {
        return this.get();
    }
}
