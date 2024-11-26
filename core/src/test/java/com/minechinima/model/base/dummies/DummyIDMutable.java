package com.minechinima.model.base.dummies;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.datainterfaces.IDMutable;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DummyIDMutable extends BaseStructure implements IDMutable {
    public DummyIDMutable(String id) {
        super(id);
    }

    protected DummyIDMutable(String id, @NotNull UUID uuid) {
        super(id, uuid);
    }

    @Override
    public BaseType<?> toData() {
        return null;
    }

    @Override
    public void fromData(BaseType<?> data) {

    }

    @Override
    public void setID(String identifier) {
        this._setID(identifier);
    }

    @Override
    public @NotNull UUID getUUID() {
        return this.uuid;
    }
}
