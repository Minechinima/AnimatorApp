package com.minechinima.model.proxy;

import com.minechinima.animation.IAnimationContext;
import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.logging.DataOperationBus;
import com.minechinima.model.proxy.backend.impl.BackendLayerHandler;
import com.minechinima.model.proxy.backend.api.BackendLayer;
import com.minechinima.model.proxy.backend.api.IDataBackend;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for all proxies that use the backend system.
 * @param <T>
 */
public abstract class BaseDataProxy<T extends IDataBackend<T>> implements IData {
    /**
     * @implNote dont expose to keep modules decoupled
     */
    protected T backend;

    protected final BackendLayerHandler<T> backendLayers = new BackendLayerHandler<>();

    protected void addBackend(T backend, BackendLayer layer) {
        if (this.backendLayers.containsBackend(backend)) {
            throw new IllegalArgumentException("Backend already added to the proxy");
        }

        if (this.backend == null) {
            this.backend = backend;
        } else {
            T prev = this.backend;
            this.backend = backend;
            backend.next(prev);
        }

        this.backendLayers.addLayer(layer, backend);
    }

    @Override
    public String getIdentifier() {
        return this.backend.getIdentifier();
    }

    @Override
    public @Nullable IDataTree getParent() {
        return this.backend.getParent();
    }

    @Override
    public @Nullable IDataTree getRoot() {
        return this.backend.getRoot();
    }

    @Override
    public boolean isChildOf(IDataTree parent) {
        return this.backend.isChildOf(parent);
    }

    @Override
    public EventBus getEventBus() {
        return this.backend.getEventBus();
    }

    @Override
    public DataOperationBus getDataOperationBus() {
        return this.backend.getDataOperationBus();
    }

    protected IAnimationContext _getAnimationContext() {
        return null;
    }
}
