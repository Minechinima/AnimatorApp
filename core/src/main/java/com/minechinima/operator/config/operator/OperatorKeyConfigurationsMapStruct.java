package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OperatorKeyConfigurationsMapStruct<C extends IContext> extends BaseValueMap<OperatorKeyConfigListStruct<C>> {
    public OperatorKeyConfigurationsMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull OperatorKeyConfigListStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }
}
