package com.minechinima.model.values.base;

import com.minechinima.controller.factories.IControllerFactoryProvider;
import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.data.types.*;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Class that holds a single value that is part of the hierarchy.
 * <br><br>
 * Allows null values.
 * @param <T>
 */
public abstract class BaseSingleValueHierarchy<T extends BaseStructure> extends BaseMutableValueHierarchy<T> implements IValue<T>,
        IControllerFactoryProvider<ValueControllerFactory<T, ?>> {
    @Nullable
    private T value = null;
    private final ValueControllerFactory<T, BaseSingleValueHierarchy<T>> controller = new ValueControllerFactory<>(this);

    public BaseSingleValueHierarchy(String id) {
        super(id, ValuePath.Format.PATH);
    }

    protected BaseSingleValueHierarchy(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.PATH);
    }

    @Override
    public ValueControllerFactory<T, ? extends BaseSingleValueHierarchy<T>> controller() {
        return this.controller;
    }

    @Override
    public final Set<T> getChildren() {
        return this.value == null ? Set.of() : Set.of(this.value);
    }

    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return child != null && this.value == child;
    }

    @Nullable
    public final T get() {
        return this.value;
    }

    @Override
    public void set(@Nullable T value) {
        if (this.get() == value) return;
        final T prevValue = this.get();
        final BaseValueHierarchy valuePrevParent = value != null ? value.getParent() : null;

        this.mutate(() -> {
            this.value = value;
            /* try to remove prevValue from tree */
            if (prevValue != null) {
                prevValue.setParent(null);
            }
            if (value != null) value.setParent(this);

            return true;
        }, () -> {
            this.value = prevValue;
            if (prevValue != null) prevValue.setParent(this);
            if (value != null) value.setParent(valuePrevParent);
            return true;
        }, () -> new ValueChangeEvent(this, prevValue, value));
    }

    @Override
    public final boolean removeChild(T child) {
        if (child == null || this.get() != child) return false;

        this.set(null);
        return this.get() == null;
    }

    public final boolean addChild(T child) {
        if (child == null || this.get() != null) return false;

        this.set(child);
        return this.get() == child;
    }

    @Override
    @Nullable
    public T valueFromData(BaseType<?> data) throws BaseTypeCannotBeParsed {
        if (data instanceof NullType) return null;
        if (data instanceof MapType mapType) {
            String id = mapType.getStringValue("id");
            UUID uuid = mapType.getUUIDValue("uuid");
            return this.instantiate(id, uuid, mapType.get("data"));
        }

        throw new BaseTypeCannotBeParsed("BaseType is not a Map");
    }

    @Override
    @Nullable
    public BaseType<?> valueToData(T obj) {
        if (obj == null) return NullType.INSTANCE;
        Map<String, BaseType<?>> map = new HashMap<>();
        map.put("id", new StringType(obj.getIdentifier()));
        map.put("data", obj.toData());

        if (obj.getID().uuid() != null) {
            map.put("uuid", new UUIDType(obj.getID().uuid()));
        }

        return new MapType(map);
    }

    @Nullable
    protected abstract T instantiate(String id, @Nullable UUID uuid, BaseType<?> data);

    @Override
    public void fromData(BaseType<?> data) {
        try {
            this.set(this.valueFromData(data));
        } catch (BaseTypeCannotBeParsed e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData(this.get());
    }
}
