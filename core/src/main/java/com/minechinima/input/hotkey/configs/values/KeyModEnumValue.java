package com.minechinima.input.hotkey.configs.values;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValue;
import com.minechinima.model.values.AbstractEnumValueReg;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class KeyModEnumValue extends AbstractEnumValueReg<KeyModEnum> {
    public KeyModEnumValue(String id, KeyModEnum defaultValue) {
        super(id, defaultValue, KeyModEnum.REGISTRY);
    }
}
