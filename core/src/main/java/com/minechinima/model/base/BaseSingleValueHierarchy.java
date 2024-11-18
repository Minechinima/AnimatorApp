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

    @Override
    public final Set<T> getChildren() {
        return this.value == null ? Set.of() : Set.of(this.value);
    }

    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return child != null && this.value != null && this.value == child;
    }

    public final T get() {
        return this.value;
    }

    @Override
    public void set(T value) {
        if (this.value == value) return;
        final T prevValue = this.value;
        final BaseType<?> valueBefore;
        final BaseType<?> valueAfter;

        /* first operation: try removing from tree */
        try {
            this.value = null;
            /* execute this here in case it was overridden badly and causes an exception */
            valueBefore = this.valueToData(prevValue);
            valueAfter = this.valueToData(value);
            /* serialization worked -> allows triggering change event later. Now proceed with tree modifying */
            prevValue.setParent(null);
        } catch (SetParentNotAllowed e) {
            this.value = prevValue;
            throw new UnsupportedOperationException(e);
        }

        /* now try adding the child */
        try {
            this.value = value;
            this.value.setParent(this);
        } catch (SetParentNotAllowed e) {
            this.value = null;
            //TODO what to do with null in serialization?
            /* the only successful operation until here was the removal -> we need to trigger this to allow undoing it */
            this.triggerEvent(new ValueChangeEvent(this, valueBefore, null));
            throw new UnsupportedOperationException(e);
        }

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
    public void fromData(BaseType<?> data) {
        try {
            this.set(this.valueFromData(data));
        } catch (BaseTypeCannotBeParsed e) {
            e.printStackTrace();
        }
    }

    @Override
    public BaseType<?> toData() {
        return this.valueToData(this.value);
    }
}
