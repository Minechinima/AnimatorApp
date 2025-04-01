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

public class MouseKeyEnumValue extends AbstractEnumValue<MouseKeyEnum> {
    public MouseKeyEnumValue(String id, MouseKeyEnum defaultValue) {
        super(id, defaultValue);
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }

    @Override
    public void fromData(BaseType<?> data) {
        MouseKeyEnum v = RegistryUtils.deserialize(data, MouseKeyEnum.REGISTRY);
        if (v != null) this.set(v);
    }

    @Override
    public BaseType<?> valueToData() {
        return RegistryUtils.serialize(this.get());
    }

    @Override
    public @Nullable MouseKeyEnum valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        MouseKeyEnum v = RegistryUtils.deserialize(data, MouseKeyEnum.REGISTRY);
        if (v == null) throw new BaseTypeCannotBeParsed("");
        return v;
    }

    @Override
    public Map<Integer, MouseKeyEnum> getAvailableValues() {
        return CollectionUtils.toMap(MouseKeyEnum.values());
    }

    @Override
    public Integer getCurrentValue() {
        return this.get().ordinal();
    }
}
