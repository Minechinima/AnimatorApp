package com.minechinima.input.hotkey.configs.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class KeyboardKeyEnumValue extends AbstractEnumValue<KeyboardKeyEnum> {
    public KeyboardKeyEnumValue(String id, @NotNull KeyboardKeyEnum defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        KeyboardKeyEnum v = RegistryUtils.deserialize(data, KeyboardKeyEnum.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return RegistryUtils.serialize(KeyboardKeyEnum.REGISTRY, this.get());
    }

    @Override
    public @Nullable KeyboardKeyEnum valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        KeyboardKeyEnum v = RegistryUtils.deserialize(data, KeyboardKeyEnum.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("Wrong data format");
        return v;
    }

    @Override
    public Map<Integer, KeyboardKeyEnum> getAvailableValues() {
        return CollectionUtils.toMap(KeyboardKeyEnum.values());
    }

    @Override
    public Integer getCurrentValue() {
        return this.get().ordinal();
    }
}
