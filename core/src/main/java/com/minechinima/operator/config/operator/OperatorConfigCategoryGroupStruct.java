package com.minechinima.operator.config.operator;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.ui.elements.window.UIRegion;
import com.minechinima.ui.elements.window.UISpace;
import org.jetbrains.annotations.Nullable;

/**
 * Operator configuration category that can have subcategories.
 * A category is for a specific UI space and regiontype.
 * @param <C>
 */
public class OperatorConfigCategoryGroupStruct<C extends IContext> extends ValueGroup {
    public final OperatorConfigCategoriesMapStruct<C> subcategories;
    public final OperatorConfigsListStruct<C> configs;

    /**
     * UI Space these configurations should be applied to.
     * If null it means it can be applied to any space.
     */
    @Nullable
    private final Class<? extends UISpace> spacetype;
    /**
     * UI Region these configurations should be applied to.
     * If null it means it can be applied to any region.
     */
    @Nullable
    private final Class<? extends UIRegion> regiontype;

    private final UserFunctionalityConfigGroupStruct<C> opConfig;

    public OperatorConfigCategoryGroupStruct(String id, UserFunctionalityConfigGroupStruct<C> opConfig) {
        this(id, null, null, opConfig);
    }

    public OperatorConfigCategoryGroupStruct(String id, Class<? extends UISpace<C>> spacetype, UserFunctionalityConfigGroupStruct<C> opConfig) {
        this(id, spacetype, null, opConfig);
    }

    public OperatorConfigCategoryGroupStruct(String id, Class<? extends UISpace<C>> spacetype, Class<? extends UIRegion<C>> regionType, UserFunctionalityConfigGroupStruct<C> opConfig) {
        super(id);
        this.addChild("categories", this.subcategories = new OperatorConfigCategoriesMapStruct<>("categories", opConfig));
        this.addChild("configs", this.configs = new OperatorConfigsListStruct<>("configs"));
        this.spacetype = spacetype;
        this.regiontype = regionType;
        this.opConfig = opConfig;
    }

    /**
     * @return
     * @implNote needs to be final as it is used for caching.
     */
    public final @Nullable Class<? extends UISpace> getSpacetype() {
        return spacetype;
    }

    /**
     * @return
     * @implNote needs to be final as it is used for caching.
     */
    public final @Nullable Class<? extends UIRegion> getRegiontype() {
        return regiontype;
    }

    public OperatorConfigGroupStruct<C> newConfig() {
        OperatorConfigGroupStruct<C> config = new OperatorConfigGroupStruct<>(this.opConfig);
        config.initChildren();
        this.configs.add(config);
        return config;
    }

    public OperatorConfigCategoryGroupStruct<C> newCategory(String id) {
        OperatorConfigCategoryGroupStruct<C> category = new OperatorConfigCategoryGroupStruct<>(id, this.opConfig);
        category.initChildren();
        this.subcategories.add(category);
        return category;
    }
}
