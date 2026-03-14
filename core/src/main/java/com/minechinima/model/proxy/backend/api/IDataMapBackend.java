package com.minechinima.model.proxy.backend.api;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.values.IDataMap;

public interface IDataMapBackend<T extends IData> extends IDataBackend<IDataMapBackend<T>>, IDataMap<T> {
    void add(T value);
}
