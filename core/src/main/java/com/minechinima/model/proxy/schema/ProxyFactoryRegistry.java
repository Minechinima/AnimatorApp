package com.minechinima.model.proxy.schema;

import com.minechinima.model.api.IData;
import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.backend.impl.database.BasestructureProxyPool;

import java.util.HashMap;

public class ProxyFactoryRegistry {
    private final HashMap<Class<? extends BaseDataProxy>, BackendBuilder> registry = new HashMap<>();

    public <T extends BaseDataProxy<?>> void register(Class<T> proxyClass, BackendBuilder<T> factory) {
        this.registry.put(proxyClass, factory);
    }

    public void parseDataClass(Class<? extends IData> dataClass, BasestructureProxyPool pool) {
        -
    }
}

