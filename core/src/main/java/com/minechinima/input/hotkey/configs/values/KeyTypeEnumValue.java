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

public class KeyTypeEnumValue extends AbstractEnumValue<KeyType> {
    public KeyTypeEnumValue(String id, @NotNull KeyType defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        KeyType v = RegistryUtils.deserialize(data, KeyType.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return RegistryUtils.serialize(KeyType.REGISTRY, this.get());
    }

    @Override
    public @Nullable KeyType valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        KeyType v = RegistryUtils.deserialize(data, KeyType.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, KeyType> getAvailableValues() {
        return CollectionUtils.toMap(KeyType.values());
    }

    @Override
    public Integer getCurrentValue() {
        return this.get().ordinal();
    }
}
