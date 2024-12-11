package com.minechinima.model.base.dummies;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseSingleValueHierarchy;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyBaseSingleValueHierarchy extends BaseSingleValueHierarchy<BaseStructure> {
    public DummyBaseSingleValueHierarchy(String id) {
        super(id);
    }

    protected DummyBaseSingleValueHierarchy(String id, UUID uuid) {
        super(id, uuid);
    }

    @NotNull
    @Override
    protected BaseStructure instantiate(String id, @Nullable UUID uuid, BaseType data) {
        return null;
    }
}
