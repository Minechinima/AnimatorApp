package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.values.base.BaseValue;
import com.minechinima.model.proxy.backend.api.IDataValueBackend;
import org.jetbrains.annotations.Nullable;

public class DatabaseValue<V> extends DatabaseBackend<BaseValue<V>, IDataValueBackend<V>> implements IDataValueBackend<V> {
    public DatabaseValue(BaseValue<V> value, BasestructureProxyPool context) {
        super(value, context);
    }

    @Override
    public @Nullable V get() {
        return this.database.get();
    }

    @Override
    public @Nullable V getDefaultValue() {
        return null;
    }

    @Override
    public void set(V value) {
        this.database.set(value);
    }
}
