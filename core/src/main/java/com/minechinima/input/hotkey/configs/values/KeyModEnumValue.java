package com.minechinima.input.hotkey.configs.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class KeyModEnumValue extends AbstractEnumValue<KeyModEnum> {
    public KeyModEnumValue(String id, KeyModEnum defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        if (data == null) return; //TODO think about null in serialization....
        KeyModEnum v = RegistryUtils.deserialize(data, KeyModEnum.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return this.get() == null ? null : RegistryUtils.serialize(KeyModEnum.REGISTRY, this.get());
    }

    @Override
    public @Nullable KeyModEnum valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        KeyModEnum v = RegistryUtils.deserialize(data, KeyModEnum.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, KeyModEnum> getAvailableValues() {
        Map<Integer, KeyModEnum> values = CollectionUtils.toMap(KeyModEnum.values());
        values.put(-1, null);
        return values;
    }

    @Override
    public Integer getCurrentValue() {
        return this.get() == null ? -1 : this.get().ordinal();
    }
}
