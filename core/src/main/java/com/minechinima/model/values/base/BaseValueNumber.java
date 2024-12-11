package com.minechinima.model.values.base;

import com.minechinima.data.types.NumberType;
import com.minechinima.utils.MathUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class BaseValueNumber<T extends Number & Comparable<T>> extends BaseValue<T> {
    protected final T min;
    protected final T max;

    public BaseValueNumber(String id, @NotNull T defaultValue, @Nullable T min, @Nullable T max) {
        super(id, defaultValue);

        this.min = min;
        this.max = max;
    }

    public BaseValueNumber(String id, @NotNull T defaultValue) {
        this(id, defaultValue, null, null);
    }

    @Nullable
    public T getMin() {
        return this.min;
    }

    @Nullable
    public T getMax() {
        return this.max;
    }

    /**
     *
     * @param value
     * @throws NullPointerException if the value argument is null.
     */
    @Override
    public void set(T value) {
        if (value == null) throw new NullPointerException("Number value cannot be null.");

        if (this.min != null && this.max != null) {
            value = MathUtils.clamp(value, this.min, this.max);
        } else if (this.min != null) {
            value = MathUtils.max(value, this.min);
        } else if (this.max != null) {
            value = MathUtils.min(value, this.max);
        }

        super.set(value);
    }

    @Override
    public abstract NumberType<T> toData();
}
