package com.minechinima.model.proxy;

import com.minechinima.model.api.IData;
import com.minechinima.model.base.BaseStructure;

/**
 * To map a database {@link BaseStructure} to its corresponding proxy. This mapping instance should be shared amongst all
 * proxies in one context where a 1-1 mapping is guaranteed.
 */
public class BasestructureProxyPool {
    //TODO the mapping must be updated when data is added/removed.
    // we might need to instantiate proxies here when data is added to the tree that
    // has no mapped proxy (especially undo/redo and remote changes).
    // idea how to retrieve proxy factory: the parent of the node is always a tree, which
    // should be a mutable tree, therefore it has a factory. In all other cases throw exception.
    public IData getProxyForNode(BaseStructure structure) {
        return null;
    }

    public BaseStructure getNodeForProxy(IData proxy) {
        return null;
    }
}
