package com.minechinima.model.proxy;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.proxy.backend.api.IDataTreeBackend;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Base class for proxies that want to proxy collections and other tree structures.
 * Also use this when the proxy is a data class, see {@link com.minechinima.model.proxy.values.Vector3fProxy}.
 * Use this class when the {@link IData} API is a {@link IDataTree}.
 * @param <T>
 */
public abstract class BaseDataTreeProxy<T extends IDataTreeBackend<T>> extends BaseDataProxy<T> implements IDataTree {
    @Override
    public Collection<? extends IData> getChildren() {
        return this.backend.getChildren();
    }

    @Override
    public boolean isDirectChild(IData child) {
        return this.backend.isDirectChild(child);
    }

    @Override
    public @Nullable IData getChildByValuePath(ValuePath path) {
        return this.backend.getChildByValuePath(path);
    }

    @Override
    public @Nullable IData getChildByUniquePath(ValuePath path) {
        return this.backend.getChildByValuePath(path);
    }

    @Override
    public boolean isUUIDPresent(UUID uuid) {
        return this.backend.isUUIDPresent(uuid);
    }
}
