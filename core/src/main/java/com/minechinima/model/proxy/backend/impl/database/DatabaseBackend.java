package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.api.IDataTree;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.logging.DataOperationBus;
import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.backend.api.IDataBackend;
import com.minechinima.model.proxy.BasestructureProxyPool;
import com.minechinima.model.proxy.backend.impl.base.BaseDataBackend;
import org.jetbrains.annotations.Nullable;

/**
 * Base for all backends that have the {@link BaseStructure} system as their source.
 * @param <D>
 * @param <T>
 */
public class DatabaseBackend<D extends BaseStructure, T extends IDataBackend<T>> extends BaseDataBackend<T> {
    protected final D database;
    protected final BasestructureProxyPool context;

    public DatabaseBackend(D database, BasestructureProxyPool context) {
        this.database = database;
        this.context = context;
    }

    @Override
    public String getIdentifier() {
        return this.database.getIdentifier();
    }

    @Override
    public @Nullable IDataTree getParent() {
        return this.context.getProxyForNode(this.database.getParent()) instanceof IDataTree tree ? tree : null;
    }

    @Override
    public @Nullable IDataTree getRoot() {
        return this.context.getProxyForNode(this.database.getRoot()) instanceof IDataTree tree ? tree : null;
    }

    @Override
    public boolean isChildOf(IDataTree parent) {
        if (parent instanceof BaseDataProxy<?> proxy && this.context.getNodeForProxy(proxy) instanceof BaseValueHierarchy<?> hierarchy) {
            return this.database.isChildOf(hierarchy);
        }
        return false;
    }

    @Override
    public EventBus getEventBus() {
        return this.database.getEventBus();
    }

    @Override
    public DataOperationBus getDataOperationBus() {
        return this.database.getDataOperationBus();
    }
}
