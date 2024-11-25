package com.minechinima.model.base.dummies;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.base.BaseValueMap;
import com.minechinima.model.values.DoubleValue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class DummyDoubleValueMap extends BaseValueMap<DoubleValue> {
    public DummyDoubleValueMap(String id) {
        super(id);
    }

    protected DummyDoubleValueMap(String id, UUID uuid) {
        super(id, uuid);
    }

    @Override
    protected @NotNull DoubleValue instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        DoubleValue test = new DoubleValue(id);
        test.fromData(data);
        return test;
    }
}
