package com.minechinima.model.app.constraints;

import com.minechinima.utils.IGenericRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

public class ConstraintRegistry implements IGenericRegistry<ConstraintStruct> {
    @Nullable
    @Override
    public ConstraintStruct getNew(int id) {
        return null;
    }

    @Nullable
    @Override
    public Integer getID(ConstraintStruct value) {
        return 0;
    }

    @Override
    public Map<Integer, ConstraintStruct> getAllNew() {
        return Map.of();
    }

    @Override
    public <J extends ConstraintStruct> void register(int id, Class<J> clazz, Supplier<J> constructor) throws IllegalArgumentException {

    }
}
