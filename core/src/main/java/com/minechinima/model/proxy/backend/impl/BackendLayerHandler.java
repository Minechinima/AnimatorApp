package com.minechinima.model.proxy.backend.impl;

import com.minechinima.model.proxy.backend.api.BackendLayer;
import com.minechinima.model.proxy.backend.api.IDataBackend;

import java.util.LinkedHashMap;

public class BackendLayerHandler<T extends IDataBackend<T>> {
    private final LinkedHashMap<BackendLayer, T> layers = new LinkedHashMap<>();

    public void addLayer(BackendLayer layer, T backend) {
        this.layers.put(layer, backend);
    }

    public T getHandlerForLayer(BackendLayer layer) {
        return this.layers.get(layer);
    }

    public boolean containsBackend(IDataBackend backend) {
        return this.layers.containsValue(backend);
    }
}
