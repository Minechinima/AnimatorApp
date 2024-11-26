package com.minechinima.model.base.dummies;

import com.minechinima.model.logging.datainterfaces.IDMutable;
import com.minechinima.model.utils.ValuePath;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DummyIDMutableHierarchy extends DummyBaseMutableHierarchy implements IDMutable {
    public DummyIDMutableHierarchy(String id, ValuePath.Format pathFormat) {
        super(id, pathFormat);
    }

    protected DummyIDMutableHierarchy(String id, UUID uuid, ValuePath.Format pathFormat) {
        super(id, uuid, pathFormat);
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
