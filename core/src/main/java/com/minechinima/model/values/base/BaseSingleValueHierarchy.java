package com.minechinima.model.values.base;

import com.minechinima.controller.api.defaults.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.data.types.MapType;
import com.minechinima.data.types.StringType;
import com.minechinima.input.hotkey.HotKeyConfig;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnum;
import com.minechinima.model.base.BaseMutableValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.utils.LimitedSizeSet;
import com.minechinima.utils.UUIDUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.naming.OperationNotSupportedException;
import java.util.*;

/**
 * Class that holds a single value that is part of the hierarchy.
 * @param <T>
 */
public abstract class BaseSingleValueHierarchy<T extends BaseStructure> extends BaseMutableValueHierarchy<T> implements IValue<T> {
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
        final BaseType<?> valueBefore = this.valueToData();
        final BaseType<?> valueAfter;

        /*
         * Perform valueAfter serialization in case it causes an exception, so we can recover.
         */
        try {
            this.value = value;
            valueAfter = this.valueToData();
        } catch (Exception e){
            /* in case of exception: revert */
            this.value = prevValue;
            throw new UnsupportedOperationException(e);
        }

        /* try to remove prevValue from tree */
        if (prevValue != null) {
            try {
                prevValue.setParent(null);
            } catch (SetParentNotAllowed e) {
                this.value = prevValue;
                throw new UnsupportedOperationException(e);
            }
        }

        if (value != null) {
            try {
                value.setParent(this);
            } catch (SetParentNotAllowed e) {
                this.value = prevValue;
                if (prevValue != null) {
                    try {
                        prevValue.setParent(this);
                    } catch (SetParentNotAllowed e1) {
                        throw new Error("Could not recover from failed tree method.", e1);
                    }
                }
                throw new UnsupportedOperationException(e);
            }
        }

        this.triggerEvent(new ValueChangeEvent(this, valueBefore, valueAfter));
    }

    public ValueControllerFactory<T, BaseSingleValueHierarchy<T>> controllers() {
        return this.controller;
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
    public void fromData(BaseType<?> data) {
        try {
            this.set(this.valueFromData(data));
        } catch (BaseTypeCannotBeParsed e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData();
    }
}
