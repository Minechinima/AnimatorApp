package com.minechinima.model.values;

import com.minechinima.model.values.base.BaseValue;
import com.minechinima.ui.utils.ILangKeyProvider;

public abstract class AbstractEnumValue<T extends ILangKeyProvider> extends BaseValue<T> implements IEnumValue<T> {
    public AbstractEnumValue(String id, T defaultValue) {
        super(id, defaultValue);
    }
}
