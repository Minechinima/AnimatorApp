package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.api.IData;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.proxy.backend.api.IDataTreeBackend;
import com.minechinima.model.proxy.backend.impl.BasestructureMappingContext;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DatabaseTreeBackend<D extends BaseValueHierarchy, T extends IDataTreeBackend<T>> extends DatabaseBackend<D, T> implements IDataTreeBackend<T> {
    public DatabaseTreeBackend(D database, BasestructureMappingContext context) {
        super(database, context);
    }

    @Override
    public Collection<? extends IData> getChildren() {
        return List.of();
    }

    //TODO
    @Override
    public boolean isDirectChild(IData child) {
        return false;
    }

    @Override
    public @Nullable IData getChildByValuePath(ValuePath path) {
        return null;
    }

    @Override
    public @Nullable IData getChildByUniquePath(ValuePath path) {
        return null;
    }

    @Override
    public boolean isUUIDPresent(UUID uuid) {
        return false;
    }
}
