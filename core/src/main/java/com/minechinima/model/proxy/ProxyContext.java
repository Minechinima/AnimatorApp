package com.minechinima.model.proxy;

import com.minechinima.model.proxy.backend.api.BackendLayer;
import com.minechinima.model.proxy.backend.impl.database.BasestructureProxyPool;

import java.util.Set;

public class ProxyContext {
    private final BasestructureProxyPool pool;
    private final Set<BackendLayer> layerToExclude;
    private final Set<BackendLayer> layersToInclude;

    public ProxyContext(BasestructureProxyPool pool, Set<BackendLayer> layerToExclude, Set<BackendLayer> layersToInclude) {
        this.pool = pool;
        this.layerToExclude = layerToExclude;
        this.layersToInclude = layersToInclude;
    }
}
