package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.api.IData;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.proxy.backend.api.IDataTreeBackend;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DatabaseTreeBackend<D extends BaseValueHierarchy, T extends IDataTreeBackend<T>> extends DatabaseBackend<D, T> implements IDataTreeBackend<T> {
    public DatabaseTreeBackend(D database, BasestructureProxyPool context) {
        super(database, context);
    }

    @Override
    public Collection<? extends IData> getChildren() {
        return List.of();
    }

    //TODO
    @Override
    public boolean isDirectChild(IData child) {
        BaseStructure node = this.context.getNodeForProxy(child);
        if (node == null) return false;
        return this.database.isDirectChild(node);
    }

    @Override
    public @Nullable IData getChildByValuePath(ValuePath path) {
        BaseStructure node = this.database.getChildByValuePath(path);
        if (node == null) return null;
        return this.context.getProxyForNode(node);
    }

    @Override
    public @Nullable IData getChildByUniquePath(ValuePath path) {
        BaseStructure node = this.database.getChildByUniquePath(path);
        if (node == null) return null;
        return this.context.getProxyForNode(node);
    }

    @Override
    public boolean isUUIDPresent(UUID uuid) {
        return this.database.isUUIDPresent(uuid);
    }
}
