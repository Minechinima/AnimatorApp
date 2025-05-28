package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * template class that utilizes {@link com.minechinima.utils.IGenericRegistry}
 * @param <T>
 */
public abstract class AbstractEnumValueHierarchyReg<T extends BaseStructure & ILangKeyProvider> extends AbstractEnumValueHierarchy<T> {
    protected final IGenericRegistry<T> registry;

    public AbstractEnumValueHierarchyReg(String id, IGenericRegistry<T> registry, T defaultValue) {
        super(id);
        this.registry = registry;
        this.set(defaultValue);
    }

    @Override
    public void set(T value) {
        if (value != null && this.registry.getID(value) == null) {
            throw new IllegalArgumentException("Tried setting the enum value to a not registered value.");
        }

        super.set(value);
    }

    @Override
    public BaseType<?> valueToData() {
        return this.get() == null ? null : RegistryUtils.serializeObjectData(this.registry, this.get());
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        return RegistryUtils.deserializeObjectData(this.registry, data);
    }

    @Override
    public Map<Integer, T> getAvailableValues() {
        Map<Integer, T> values = this.registry.getAllNew();
        /* overwrite the instance so it contains the correct data of current value */
        values.put(this.getCurrentValue(), this.get());
        return values;
    }

    @Override
    public Integer getCurrentValue() {
        return this.registry.getID(this.get());
    }
}
