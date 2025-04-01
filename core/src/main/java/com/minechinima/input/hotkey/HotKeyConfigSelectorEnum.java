package com.minechinima.input.hotkey;

import com.minechinima.controller.api.defaults.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.configs.values.KeyType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.AbstractEnumValueHierarchy;
import com.minechinima.utils.CollectionUtils;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.UUID;

/**
 * Acts like an enum where the value is the HotKey configuration.
 */
public class HotKeyConfigSelectorEnum extends AbstractEnumValueHierarchy<HotKeyConfig> {
    private final HotKeyConfigRegistry registry;

    public HotKeyConfigSelectorEnum(String id, HotKeyConfigRegistry registry) {
        super(id, registry.getNewDefault());
        this.registry = registry;
    }

    @Override
    public BaseType<?> valueToData() {
        return this.registry == null ? null : RegistryUtils.serializeObjectData(this.registry, this.get());
    }

    @Override
    public @Nullable HotKeyConfig valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        return RegistryUtils.deserializeObjectData(this.registry, data);
    }

    @Override
    public Map<Integer, HotKeyConfig> getAvailableValues() {
        Map<Integer, HotKeyConfig> values = this.registry.getAllNew();
        values.put(this.getCurrentValue(), this.get());
        return values;
    }

    @Override
    public Integer getCurrentValue() {
        return this.registry.getID(this.get());
    }
}
