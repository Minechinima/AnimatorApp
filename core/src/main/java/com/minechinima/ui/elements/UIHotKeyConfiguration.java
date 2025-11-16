package com.minechinima.ui.elements;

import com.minechinima.input.hotkey.*;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.operator.OperatorConfigCategoriesMapStruct;
import com.minechinima.operator.config.operator.OperatorConfigCategoryGroupStruct;
import com.minechinima.operator.config.operator.OperatorConfigsListStruct;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.Map;

public class UIHotKeyConfiguration<C extends IContext> extends UIElement<C> {

    public UIHotKeyConfiguration() {

    }

    @Override
    public void draw(UIContext<C> context) {
        UserFunctionalityConfigGroupStruct<C> config = context.getUIManager().getOperatorManager().getConfig();
        this.drawConfigCategories(context, config.operators);
    }

    private void drawConfigCategories(UIContext<C> context, OperatorConfigCategoriesMapStruct<C> config) {
        for (Map.Entry<String, OperatorConfigCategoryGroupStruct<C>> category : config) {
            if (ImGui.collapsingHeader(context.getTranslationBackpack().getTranslation(category.getValue().getIdentifier() + ".title",
                    context.getLocale()))) {
                this.drawConfigCategories(context, category.getValue().subcategories);
                this.drawConfigs(context, category.getValue().configs);
            }
        }

    }

    private void drawConfigs(UIContext<C> context, OperatorConfigsListStruct<C> config) {
        for (OperatorConfigGroupStruct<C> cfg : config) {
            UIEnum<HotKeyConfigGroupStruct, HotKeyConfigSelectorEnumStruct, C> hotkey = new UIEnum<>("hotkey");
            hotkey.init(cfg.hotKeyConfigSelector);
            hotkey.draw(context);

            if (hotkey.getValue().get() != null) {
                context.getUiHandlerRegistry().getUnnamedHandlersFor(hotkey.getValue().get()).forEach(
                        hotKeyConfigIUIHandler -> {
                            hotKeyConfigIUIHandler.draw(context, hotkey.getValue().get());
                        });
            }


            if (cfg.props.getProps() != null) {
                context.getUiHandlerRegistry().getUnnamedHandlersFor(cfg.props.getProps()).forEach(
                        hotKeyConfigIUIHandler -> {
                            hotKeyConfigIUIHandler.draw(context, cfg.props.getProps());
                        });
            }

            UIPointerTargetSelector<OperatorType, C> uiptr = new UIPointerTargetSelector<>("OperatorType");
            uiptr.setSourceList((BaseValueHierarchy<OperatorType>) cfg.operatorType.getReferenceFrame());
            uiptr.init(cfg.operatorType).draw(context);
        }
    }
}
