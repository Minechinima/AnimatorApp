package com.minechinima.model.app.constraints;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.exceptions.BaseTypeCannotBeParsed;
import com.minechinima.model.values.base.BaseValueOrderedMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class Constraints extends BaseValueOrderedMap<ConstraintContainer> {
    public Constraints(String id) {
        super(id);
    }

    @NotNull
    @Override
    protected ConstraintContainer instantiate(String id, @Nullable UUID uuid, BaseType<?> map) throws BaseTypeCannotBeParsed {
        return null;
    }
}
