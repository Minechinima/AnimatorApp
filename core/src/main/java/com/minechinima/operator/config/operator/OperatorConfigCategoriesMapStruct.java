package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * Map of operator config categories.
 * <br><br>
 * @param <C>
 * @implSpec is a map to retrieve categories by a key for easier runtime read/write access.
 */
public class OperatorConfigCategoriesMapStruct<C extends IContext> extends BaseValueMap<OperatorConfigCategoryGroupStruct<C>> {
    public OperatorConfigCategoriesMapStruct(String id) {
        super(id);
    }

    @Override
    protected @NotNull OperatorConfigCategoryGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, id);
        category.fromData(data);
        category.initChildren();
        return category;
    }
}
