package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Category of property modifiers for an operatorpoperties.<br>
 * Is a list of configs, each config has the property modifier type
 * and the hotkey config.
 * <br><br>
 * This is at the same time the registry for instantiating the ProperyModifier instances for the config enums.
 * @param <P>
 */
public class PropertyModifiersListStruct<P extends OperatorPropertiesGroupStruct> extends BaseValueList<PropertyModifierHotKeyConfig<P>>
        implements IGenericRegistry<PropertyModifierConfig<P>> {
    public final HotKeyConfigRegistry hotKeyConfigRegistry;
    public final Class<P> clazz;
    private final GenericRegistry<PropertyModifierConfig<P>> registry = new GenericRegistry<>();

    public PropertyModifiersListStruct(String id, Class<P> propClazz, HotKeyConfigRegistry hotKeyConfigRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.clazz = propClazz;
    }

    public PropertyModifierHotKeyConfig<P> newConfig() {
        PropertyModifierHotKeyConfig<P> cfg = new PropertyModifierHotKeyConfig<P>(this);
        cfg.initChildren();
        this.add(cfg);
        return cfg;
    }

    @Override
    public @Nullable PropertyModifierConfig<P> getNew(int id) {
        return this.registry.getNew(id);
    }

    @Override
    public @Nullable Integer getID(PropertyModifierConfig<P> value) {
        return this.registry.getID(value);
    }

    @Override
    public Map<Integer, PropertyModifierConfig<P>> getAllNew() {
        return this.registry.getAllNew();
    }

    @Override
    public <J extends PropertyModifierConfig<P>> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
        this.registry.register(id, clazz, constructor);
    }

    @Override
    protected @NotNull PropertyModifierHotKeyConfig<P> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
        return null;
    }
}
