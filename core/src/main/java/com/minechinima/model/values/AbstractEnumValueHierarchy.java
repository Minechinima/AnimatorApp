package com.minechinima.model.values;

import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.values.base.BaseValue;
import com.minechinima.ui.utils.IUIEnumValue;
import com.minechinima.ui.utils.ILangKeyProvider;

/**
 * Acts like an enum value but the value is part of the tree structure.
 * @param <T>
 */
public abstract class AbstractEnumValueHierarchy<T extends BaseStructure & ILangKeyProvider> extends BaseSingleValueHierarchy<T> implements IUIEnumValue<T> {
    public AbstractEnumValueHierarchy(String id, T defaultValue) {
        super(id);
        this.set(defaultValue);
    }

    public AbstractEnumValueHierarchy(String id) {
        super(id);
    }

    @Override
    public T getCurrentValue() {
        return this.get();
    }
}
