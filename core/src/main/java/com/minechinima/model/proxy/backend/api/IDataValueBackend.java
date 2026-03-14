package com.minechinima.model.proxy.backend.api;

import com.minechinima.model.api.values.IDataValue;

public interface IDataValueBackend<V> extends IDataBackend<IDataValueBackend<V>>, IDataValue<V> {
    void set(V value);
}
