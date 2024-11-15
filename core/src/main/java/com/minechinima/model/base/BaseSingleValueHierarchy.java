package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.dataoperations.datainterfaces.IValue;
import com.minechinima.model.events.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.exceptions.SetParentNotAllowed;

import java.util.*;

/**
 * Class that holds a single value that is part of the hierarchy.
 * @param <T>
 */
public abstract class BaseSingleValueHierarchy<T extends BaseStructure> extends BaseMutableValueHierarchy<T> implements IValue<T> {
    private T value;

    public BaseSingleValueHierarchy(String id) {
        super(id, ValuePath.Format.PATH);
    }

    protected BaseSingleValueHierarchy(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.PATH);
    }

    public final T get() {
        return this.value;
    }

    @Override
    public void set(T value) {
        if (this.value == value) return;
        BaseType<?> valueBefore = this.valueToData();
        final T prevValue = this.value;
        this.value = value;

        try {
            this.value.setParent(this);
        } catch (SetParentNotAllowed e) {
            this.value = prevValue;
            throw new RuntimeException(e);
        }

        BaseType<?> valueAfter;
        /* safety first, if something terminates here, we cannot log the change. */
        try {
            valueAfter = this.valueToData();
            if (prevValue != null) prevValue.clearNodeFromMemory();
        } catch (Exception e) {
            this.value.setParentNull();
            this.value = prevValue;
            throw new RuntimeException(e);
        }
        if (prevValue != null) prevValue.setParentNull();

        this.triggerEvent(new ValueChangeEvent(this, valueBefore, valueAfter));
    }

    @Override
    public final boolean removeChild(T child) {
        if (child == null || this.value != child) return false;

        this.set(null);
        return this.value == null;
    }

    @Override
    public final boolean addChild(T child) {
        if (child == null || this.value != null) return false;

        this.set(child);
        return this.value == child;
    }

    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return child != null && this.value != null && this.value == child;
    }

    @Override
    public final Set<T> getChildren() {
        return Set.of(this.value);
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
