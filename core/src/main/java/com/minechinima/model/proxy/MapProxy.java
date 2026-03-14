package com.minechinima.model.proxy;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.values.IDataMap;
import com.minechinima.model.proxy.backend.api.BackendLayer;
import com.minechinima.model.proxy.backend.api.IDataMapBackend;

import java.util.UUID;

public class MapProxy<T extends IData> extends BaseDataProxy<IDataMapBackend<T>> implements IDataMap<T> {
    @Override
    public int size() {
        return this.backend != null ? this.backend.size() : 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public boolean containsUUID(UUID uuid) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(String key) {
        return null;
    }

    @Override
    public T get(UUID uuid) {
        return null;
    }

    public void add(T value, BackendLayer layer) {
        IDataMapBackend<T> backend = this.backendLayers.getHandlerForLayer(layer);
        if (backend != null) {
            backend.add(value);
        }
    }
}
