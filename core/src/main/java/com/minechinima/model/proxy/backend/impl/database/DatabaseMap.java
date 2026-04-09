package com.minechinima.model.proxy.backend.impl.database;

import com.minechinima.model.api.IData;
import com.minechinima.model.proxy.BaseDataProxy;
import com.minechinima.model.proxy.backend.api.IDataMapBackend;
import com.minechinima.model.values.base.BaseValueMap;

import java.util.Set;
import java.util.UUID;

public class DatabaseMap<T extends IData> extends DatabaseTreeBackend<BaseValueMap<?>, IDataMapBackend<T>> implements IDataMapBackend<T> {
    public DatabaseMap(BaseValueMap<?> database, BasestructureProxyPool context) {
        super(database, context);
    }

    @Override
    public void add(T value) {
        // TODO mapping and multiple contexts is weird, controller is executed through UI on depsgraph context.
        //  which means the proxy is the proxy from the evaluated copy but through the mapping we should be able to get the
        //  central database BaseStructure instance...
        if (!(value instanceof BaseDataProxy<?> proxy)) return;
        //TODO basestructure collections now need to accept any basestructure, how do we ensure type safety?
        //this.database.add(this.context.getNodeForProxy(proxy));
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public boolean containsKey(String key) {
        return false;
    }

    @Override
    public boolean containsUUID(UUID uuid) {
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        return false;
    }

    @Override
    public T get(String key) {
        return null;
    }

    @Override
    public T get(UUID uuid) {
        return null;
    }

    @Override
    public Set<T> values() {
        return Set.of();
    }
}
