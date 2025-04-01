package com.minechinima.model.values.base;

import com.minechinima.controller.api.defaults.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

//TODO add some mechanism for final attribute so a value can be completely final and never change - locked attribute is a UI specific thing, see Blender
public abstract class BaseValue<T> extends BaseStructure implements IValue<T> {
    private T value;
    private final T defaultValue;
    private final ValueControllerFactory<T, BaseValue<T>> controllers = new ValueControllerFactory<>(this);

    public BaseValue(String id) {
        this(id, null);
    }

    public BaseValue(String id, T defaultValue) {
        super(id);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    /**
     * Triggers {@link ValueChangeEvent}
     * @param value
     */
    @MustBeInvokedByOverriders
    public void set(T value) {
        if (this.value == value) return;
        BaseType<?> valueBefore = this.valueToData();
        final T prevValue = this.value;
        this.value = value;
        BaseType<?> valueAfter;
        /* safety first, if something terminates here, we cannot log the change. */
        try {
            valueAfter = this.valueToData();
        } catch (Exception e) {
            this.value = prevValue;
            throw new RuntimeException(e);
        }

        this.triggerEvent(new ValueChangeEvent(this, valueBefore, valueAfter));
    }

    /**
     * This is final to ensure that subclasses don't detach their value behavior from the contract enforced here.
     * It is important that the set method is called as it has important event methods.
     * @return this value reference.
     */
    public final T get() {
        return this.value;
    }

    public T getDefaultValue() {
        return this.defaultValue;
    }

    public boolean isChanged() {
        return (this.value == null && this.defaultValue != null) || (this.value != null && !this.value.equals(this.defaultValue));
    }

    public ValueControllerFactory<T, BaseValue<T>> controllers() {
        return this.controllers;
    }
}
