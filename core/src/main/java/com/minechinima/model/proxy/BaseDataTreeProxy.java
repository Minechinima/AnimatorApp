package com.minechinima.model.proxy;

import com.minechinima.model.api.IData;
import com.minechinima.model.api.IDataTree;
import com.minechinima.model.proxy.backend.api.IDataTreeBackend;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BaseDataTreeProxy<T extends IDataTreeBackend<T>> extends BaseDataProxy<T> implements IDataTree {
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
