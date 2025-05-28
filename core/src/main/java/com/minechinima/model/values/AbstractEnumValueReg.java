package com.minechinima.model.values;

import com.minechinima.data.types.BaseType;
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

    @Override
    public void set(T value) {
        if (value != null && this.registry.getID(value) == null) {
            throw new IllegalArgumentException("Tried setting the enum value to a not registered value.");
        }

        super.set(value);
    }


    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        //TODO check all the enum datastructs for null... this is temporary and draft, null not implemented well.
        if (data == null) {
            this.set(null);
            return;
        }
        T v = RegistryUtils.deserialize(data, this.registry);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return this.get() == null ? null : RegistryUtils.serialize(this.registry, this.get());
    }

    @Override
    public @Nullable T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        return RegistryUtils.deserialize(data, this.registry);
    }

    @Override
    public Map<Integer, T> getAvailableValues() {
        return this.registry.getAllNew();
    }

    @Override
    public Integer getCurrentValue() {
        return this.get() == null ? -1 : this.registry.getID(this.get());
    }
}
