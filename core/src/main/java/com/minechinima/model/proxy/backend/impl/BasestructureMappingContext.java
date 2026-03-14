package com.minechinima.model.proxy.backend.impl;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.proxy.BaseDataProxy;

/**
 * To map a database {@link BaseStructure} to its corresponding proxy. This mapping instance should be shared amongst all
 * proxies in one context where a 1-1 mapping is guaranteed.
 */
public class BasestructureMappingContext {
    public BaseDataProxy getProxyForNode(BaseStructure structure) {
        return null;
    }

    public BaseStructure getNodeForProxy(BaseDataProxy proxy) {
        return null;
    }
}
