package com.minechinima.operator.config.modal;

import com.minechinima.data.types.BaseType;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueList;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.IOTModalModifierProvider;
import com.minechinima.utils.GenericRegistry;
import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

/**
 * Category of property modifiers for an operatorpoperties type.<br>
 * Is a list of configs, each config has the property modifier type
 * and the hotkey config.
 * <br><br>
 * This is at the same time the registry for instantiating the ProperyModifier instances for the config enums.
 * @param <P>
 */
public class PropertyModifiersList<P extends OperatorProperties> extends BaseValueList<PropertyModifierHotKeyConfig<P>>
        implements IGenericRegistry<PropertyModifierCommandConfig<P>> {
    /**
     * The registry to create hotkeys for the modifiers in this.
     */
    public final IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry;
    /**
     * Type of the OperatorProperties. Needed for type safety with generics in contexts
     * where type is unknown.
     */
    public final Class<P> clazz;
    protected final IGenericRegistry<PropertyModifierCommandConfig<P>> registry;
    /**
     * Set of operatortypes that use this modal keymap.
     */
    protected final Set<IOTModalModifierProvider<?, ?, ? extends P>> operatorsUsingThis = new HashSet<>();

    public PropertyModifiersList(String id, Class<P> propClazz, IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry) {
        this(id, propClazz, new GenericRegistry<>(), hotKeyConfigRegistry);
    }

    public PropertyModifiersList(String id, Class<P> propClazz, IGenericRegistry<PropertyModifierCommandConfig<P>> registry, IGenericRegistry<HotKeyConfigGroupStruct> hotKeyConfigRegistry) {
        super(id);
        this.hotKeyConfigRegistry = hotKeyConfigRegistry;
        this.clazz = propClazz;
        this.registry = registry;
    }

    /**
     * Register an operatorType that uses this.
     * @param operatorType
     */
    public void addOperatorType(IOTModalModifierProvider<?, ?, ? extends P> operatorType) {
        this.operatorsUsingThis.add(operatorType);
    }

    public PropertyModifierHotKeyConfig<P> newConfig() {
        PropertyModifierHotKeyConfig<P> cfg = new PropertyModifierHotKeyConfig<P>(this);
        cfg.initChildren();
        this.add(cfg);
        return cfg;
    }

    @Override
    public @Nullable PropertyModifierCommandConfig<P> getNew(int id) {
        return this.registry.getNew(id);
    }

    @Override
    public @Nullable Integer getID(PropertyModifierCommandConfig<P> value) {
        return this.registry.getID(value);
    }

    @Override
    public Map<Integer, PropertyModifierCommandConfig<P>> getAllNew() {
        return this.registry.getAllNew();
    }

    @Override
    public <J extends PropertyModifierCommandConfig<P>> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {
        this.registry.register(id, clazz, constructor);
    }

    @Override
    protected @NotNull PropertyModifierHotKeyConfig<P> instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
        //TODO maybe add a utility class for maps/lists that use a registry with integer id to instantiate
        return null;
    }
}
