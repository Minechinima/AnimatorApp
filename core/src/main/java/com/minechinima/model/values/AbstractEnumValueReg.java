package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.NullType;
import com.minechinima.input.hotkey.configs.values.KeyboardKeyEnum;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * template class that utilizes {@link com.minechinima.utils.IGenericRegistry}
 * @param <T>
 */
public class AbstractEnumValueReg<T extends ILangKeyProvider> extends AbstractEnumValue<T> {
    private final IGenericRegistry<T> registry;

    public AbstractEnumValueReg(String id, T defaultValue, IGenericRegistry<T> registry) {
        super(id, defaultValue);
        this.registry = registry;
    }

    public IGenericRegistry<T> getRegistry() {
        return this.registry;
    }

    @Override
    public void set(T value) {
        if (value != null && this.registry.getID(value) == null) {
            throw new IllegalArgumentException("Tried setting the enum value to a not registered value.");
        }

        super.set(value);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData(this.get());
    }

    @Override
    public void fromData(BaseType<?> data) {
        if (data instanceof NullType) {
            this.set(null);
            return;
        }
        T v = RegistryUtils.deserialize(data, this.registry);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData(T obj) {
        return obj == null ? NullType.INSTANCE : RegistryUtils.serialize(this.registry, obj);
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        return data instanceof NullType ? null : RegistryUtils.deserialize(data, this.registry);
    }

    @Override
    public Map<Integer, T> getAvailableValues() {
        return this.registry.getAllNew();
    }

    @Override
    @Nullable
    public Integer getCurrentValueIndex() {
        return this.get() == null ? null : this.registry.getID(this.get());
    }
}
