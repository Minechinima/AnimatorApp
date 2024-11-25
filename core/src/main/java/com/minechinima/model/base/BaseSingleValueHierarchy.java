package com.minechinima.model.base;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.utils.BaseOperationsUtils;
import com.minechinima.model.dataoperations.datainterfaces.IValue;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.utils.ValuePath;
import com.minechinima.model.exceptions.SetParentNotAllowed;
import com.minechinima.utils.LimitedSizeSet;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Class that holds a single value that is part of the hierarchy.
 * @param <T>
 */
public abstract class BaseSingleValueHierarchy<T extends BaseStructure> extends BaseMutableValueHierarchy<T> implements IValue<T> {
    private final LimitedSizeSet<T> proxySet = new LimitedSizeSet<>(1, new HashSet<>());

    public BaseSingleValueHierarchy(String id) {
        super(id, ValuePath.Format.PATH);
    }

    protected BaseSingleValueHierarchy(String id, UUID uuid) {
        super(id, uuid, ValuePath.Format.PATH);
    }

    @Override
    public final Set<T> getChildren() {
        if (this.proxySet.size() > 1) throw new IllegalStateException("Proxy Set size is greater than 1");
        return Set.copyOf(this.proxySet);
    }

    @Override
    public final boolean isDirectChild(BaseStructure child) {
        return child != null && this.proxySet.contains(child);
    }

    public final T get() {
        if (this.proxySet.size() > 1) throw new IllegalStateException("Proxy Set size is greater than 1");
        return this.proxySet.isEmpty() ? null : this.proxySet.iterator().next();
    }

    @Override
    public void set(T value) {
        if (this.get() == value) return;
        final T prevValue = this.get();
        final BaseType<?> valueBefore = this.valueToData();
        final BaseType<?> valueAfter;

        /*
         * Perform valueAfter serialization in case it causes an exception, so we can recover.
         */
        try {
            this.proxySet.clear();
            this.proxySet.add(value);
            valueAfter = this.valueToData();
        } finally {
            /* in case of exception: revert
             * in case serialization was successful,
             * put old value back, so it can be removed from the tree */
            this.proxySet.clear();
            this.proxySet.add(prevValue);
        }

        BaseOperationsUtils.removeChildOrThrow(prevValue, this.proxySet);
        try {
            BaseOperationsUtils.addChildOrThrow(value, this, this.proxySet);
        } catch (Exception e) {
            //TODO what to do with null in serialization?
            /* the only successful operation until here was the removal -> we need to trigger this to allow undoing it */
            this.triggerEvent(new ValueChangeEvent(this, valueBefore, null));
            throw e;
        }

        this.triggerEvent(new ValueChangeEvent(this, valueBefore, valueAfter));
    }

    @Override
    public final boolean removeChild(T child) {
        if (child == null || this.get() != child) return false;

        this.set(null);
        return this.get() == null;
    }

    @Override
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
