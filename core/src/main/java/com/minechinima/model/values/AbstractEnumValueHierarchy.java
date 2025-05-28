package com.minechinima.model.values;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.ui.utils.IEnumValue;
import com.minechinima.ui.utils.ILangKeyProvider;
import org.jetbrains.annotations.NotNull;

/**
 * Acts like an enum value but the value is part of the tree structure.
 * @param <T>
 */
public abstract class AbstractEnumValueHierarchy<T extends BaseStructure & ILangKeyProvider> extends BaseSingleValueHierarchy<T> implements IEnumValue<T> {
    public AbstractEnumValueHierarchy(String id, T defaultValue) {
        super(id);
        this.set(defaultValue);
    }

    public AbstractEnumValueHierarchy(String id) {
        super(id);
    }
}
