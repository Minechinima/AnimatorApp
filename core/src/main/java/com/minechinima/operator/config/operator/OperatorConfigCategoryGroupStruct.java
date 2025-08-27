package com.minechinima.operator.config.operator;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;

/**
 * Operator configuration category that can have subcategories.
 * A category is for a specific UI space and regiontype.
 * @param <C>
 */
public class OperatorConfigCategoryGroupStruct<C extends IContext> extends ValueGroup {
    public final OperatorConfigCategoriesMapStruct<C> subcategories = new OperatorConfigCategoriesMapStruct<>("categories");
    public final OperatorKeyConfigListStruct<C> configs = new OperatorKeyConfigListStruct<>("configs");

    /* TODO space, region ui stuff
     * public final Class<? extends UI...> spacetype;
     * public final Class<? extends UI...> regiontype;
     */
    public final String translationKey;

    public OperatorConfigCategoryGroupStruct(String id, String translationKey) {
        super(id);
        this.addChild("categories", this.subcategories);
        this.addChild("configs", this.configs);
        this.translationKey = translationKey;
    }
}
