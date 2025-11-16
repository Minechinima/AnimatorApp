package com.minechinima.operator.config.operator;

import com.minechinima.data.types.BaseType;
import com.minechinima.model.values.base.BaseValueMap;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.ui.elements.window.UIRegion;
import com.minechinima.ui.elements.window.UISpace;
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
    private final UserFunctionalityConfigGroupStruct<C> opConfig;

    public OperatorConfigCategoriesMapStruct(String id, UserFunctionalityConfigGroupStruct<C> opConfig) {
        super(id);
        this.opConfig = opConfig;
    }

    @Override
    protected @NotNull OperatorConfigCategoryGroupStruct<C> instantiate(String id, @Nullable UUID uuid, BaseType<?> data) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, this.opConfig);
        category.fromData(data);
        category.initChildren();
        return category;
    }

    public OperatorConfigCategoryGroupStruct<C> addConfigCategory(String id) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, this.opConfig);
        category.initChildren();
        this.add(category);
        return category;
    }

    public OperatorConfigCategoryGroupStruct<C> addConfigCategory(String id, Class<? extends UISpace<C>> uispace) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, uispace, this.opConfig);
        category.initChildren();
        this.add(category);
        return category;
    }

    public OperatorConfigCategoryGroupStruct<C> addConfigCategory(String id,
                                                                  @Nullable Class<? extends UISpace<C>> uispace,
                                                                  @Nullable Class<? extends UIRegion<C>> uiRegion) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, uispace, uiRegion, this.opConfig);
        category.initChildren();
        this.add(category);
        return category;
    }
}
