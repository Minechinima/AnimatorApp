package com.minechinima.ui.elements;

import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.operator.OperatorConfigCategoriesMapStruct;
import com.minechinima.operator.config.operator.OperatorConfigCategoryGroupStruct;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.ui.UIContext;

import java.util.Map;
import java.util.function.Consumer;

public class UIHotKeyConfiguration<C extends IContext> extends UIElement<C> {
    private Consumer<UIContext<C>> cachedUpdate;

    public UIHotKeyConfiguration() {

    }

    @SubscribeEvent(type= SubscribeEvent.Type.IN_CHILDREN)
    private void onChange(ChangeEvent event) {
        this.cachedUpdate = this::_onShow;
    }

    @Override
    protected void _onShow(UIContext<C> context) {
        UserFunctionalityConfigGroupStruct<C> config = context.getUIManager().getOperatorManager().getConfig();
        //config.getEventBus().subscribe(this);

        this.createUI(context, config.operators);
    }

    protected void createUI(UIContext<C> context, OperatorConfigCategoriesMapStruct<C> config) {
        this.children.clear();

        for (Map.Entry<String, OperatorConfigCategoryGroupStruct<C>> category : config) {
            UIAccordion<C> uiAccordion = new UIAccordion<>(category.getValue().getIdentifier() + ".title");
            this.children.add(uiAccordion);
            this.createCategories(category.getValue().subcategories, uiAccordion);

            for (OperatorConfigGroupStruct<C> cfg : category.getValue().configs) {
                UIOperatorConfigElement<C> opCfgUI = new UIOperatorConfigElement<>(cfg);
                uiAccordion.addChild(opCfgUI);
            }
        }
    }

    protected void createCategories(OperatorConfigCategoriesMapStruct<C> config, UIAccordion<C> parentAccordion) {
        for (Map.Entry<String, OperatorConfigCategoryGroupStruct<C>> category : config) {
            UIAccordion<C> uiAccordion = new UIAccordion<>(category.getValue().getIdentifier() + ".title");
            parentAccordion.addChild(uiAccordion);
            this.createCategories(category.getValue().subcategories, uiAccordion);

            for (OperatorConfigGroupStruct<C> cfg : category.getValue().configs) {
                UIOperatorConfigElement<C> opCfgUI = new UIOperatorConfigElement<>(cfg);
                uiAccordion.addChild(opCfgUI);
            }
        }
    }

    @Override
    public void draw(UIContext<C> context) {
        if (this.cachedUpdate != null) {
            this.cachedUpdate.accept(context);
            this.cachedUpdate = null;
        }
    }
}
