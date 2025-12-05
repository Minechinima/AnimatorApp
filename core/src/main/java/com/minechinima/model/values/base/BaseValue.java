package com.minechinima.model.values.base;

import com.minechinima.controller.factories.IControllerFactoryProvider;
import com.minechinima.controller.factories.ValueControllerFactory;
import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IValue;
import com.minechinima.model.events.types.changes.ValueChangeEvent;
import org.jetbrains.annotations.MustBeInvokedByOverriders;

public abstract class BaseValue<T> extends BaseStructure implements IValue<T>, IControllerFactoryProvider<ValueControllerFactory<T, ?>> {
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

    @Override
    public ValueControllerFactory<T, ? extends BaseValue<T>> controller() {
        return this.controllers;
    }

    /**
     * Triggers {@link ValueChangeEvent}
     * @param value
     */
    @MustBeInvokedByOverriders
    public void set(T value) {
        if (this.value == value) return;
        final T prevValue = this.value;
        this.value = value;
        /* safety first, if something terminates here, we cannot log the change. */
        final ValueChangeEvent changeEvent;
        try {
            changeEvent = new ValueChangeEvent(this, prevValue, value);
        } catch (Exception e) {
            this.value = prevValue;
            throw new RuntimeException(e);
        }

        this.triggerEvent(changeEvent);
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
}
