package com.minechinima.model.base;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.data.types.BaseType;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DummyValueMap extends BaseValueMap<DummyValueGroup> {
    public DummyValueMap(String id) {
        super(id);
    }

    protected DummyValueMap(String id, UUID uuid) {
        super(id, uuid);
    }

    @Override
    protected @NotNull DummyValueGroup instantiate(String id, UUID uuid, BaseType<?> data) {
        if (data != null && data.isMap()) return new DummyValueGroup(id, data.asMapType());
        return new DummyValueGroup(id);
    }

    @Override
    public boolean canAdd(Object value) throws UserException {
        return true;
    }
}
