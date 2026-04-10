package com.minechinima.model.proxy.schema;

import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.ProxyContext;

public interface BackendBuilder<T extends BaseDataProxy<?>> {
    /**
     * Build the backend layers for a given proxy.
     * @param proxy
     * @param context
     * @throws IllegalArgumentException if the proxy already has backend layers.
     */
    void buildBackend(T proxy, ProxyContext context);
}
