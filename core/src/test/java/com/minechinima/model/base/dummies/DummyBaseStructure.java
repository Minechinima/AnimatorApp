package com.minechinima.model.base.dummies;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseStructure;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DummyBaseStructure extends BaseStructure {
    public DummyBaseStructure(String id) {
        super(id);
    }

    protected DummyBaseStructure(String id, @NotNull UUID uuid) {
        super(id, uuid);
    }

    @Override
    public BaseType<?> toData() {
        return null;
    }

    @Override
    public void fromData(BaseType<?> data) {

    }
}
