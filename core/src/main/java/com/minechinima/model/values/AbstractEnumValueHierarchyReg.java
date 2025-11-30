package com.minechinima.model.values;

import com.minechinima.data.types.*;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.ui.utils.ILangKeyProvider;
import com.minechinima.utils.IGenericRegistry;
import com.minechinima.utils.RegistryUtils;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Enum value that utilizes {@link com.minechinima.utils.IGenericRegistry}
 * <br>
 * <b>Important:</b> This does not support deserializing identifiers or uuids.
 * @param <T>
 */
public abstract class AbstractEnumValueHierarchyReg<T extends BaseStructure & ILangKeyProvider> extends AbstractEnumValueHierarchy<T> {
    protected final IGenericRegistry<T> registry;

    public AbstractEnumValueHierarchyReg(String id, IGenericRegistry<T> registry, T defaultValue) {
        super(id);
        this.registry = registry;
        this.set(defaultValue);
    }

    public AbstractEnumValueHierarchyReg(String id, IGenericRegistry<T> registry) {
        this(id, registry, null);
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
    protected final T instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return RegistryUtils.deserializeObjectData(this.registry, data);
    }

    @Override
    public BaseType<?> valueToData() {
        if (this.get() == null) return NullType.INSTANCE;
        return RegistryUtils.serializeObjectData(this.registry, this.get());
    }

    @Override
    @Nullable
    public T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data instanceof NullType) return null;
        return RegistryUtils.deserializeObjectData(this.registry, data);
    }

    @Override
    public Map<Integer, T> getAvailableValues() {
        Map<Integer, T> values = this.registry.getAllNew();
        /* overwrite the instance so it contains the correct data of current value */
        values.put(this.getCurrentValueIndex(), this.get());
        return values;
    }

    @Override
    public Integer getCurrentValueIndex() {
        return this.registry.getID(this.get());
    }
}
