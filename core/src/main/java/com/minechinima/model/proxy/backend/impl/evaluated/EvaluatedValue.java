package com.minechinima.model.proxy.backend.impl.evaluated;

import com.minechinima.model.proxy.backend.api.IDataValueBackend;
import com.minechinima.model.proxy.backend.impl.base.BaseDataBackend;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EvaluatedValue<V> extends BaseDataBackend<IDataValueBackend<V>> implements IDataValueBackend<V> {
    /**
     * null means the value is not evaluated and we need to fall back to other handlers.
     */
    @Nullable
    private Optional<V> value;

    @Override
    public V get() {
        return this.value == null ? this.next.get() : this.value.orElse(null);
    }

    @Override
    public @Nullable V getDefaultValue() {
        return null;
    }

    @Override
    public void set(@Nullable V value) {
        this.value = Optional.ofNullable(value);
    }
}
