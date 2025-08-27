package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class OperatorConfigCategoriesMapStruct<C extends IContext> extends BaseValueMap<OperatorConfigCategoryGroupStruct<C>> {
    public OperatorConfigCategoriesMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull OperatorConfigCategoryGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        return null;
    }
}
