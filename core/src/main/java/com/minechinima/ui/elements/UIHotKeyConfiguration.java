package com.minechinima.ui.elements;

import com.minechinima.input.hotkey.*;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.operator.OperatorKeyConfigListStruct;
import com.minechinima.operator.config.operator.OperatorKeyConfigItemGroupStruct;
import com.minechinima.operator.config.KeyConfigurationsGroupStruct;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;

import java.util.Map;

public class UIHotKeyConfiguration<C extends IContext> extends UIElement<C> {

    public UIHotKeyConfiguration() {

    }

    @Override
    public void draw(UIContext<C> context) {
        KeyConfigurationsGroupStruct<C> config = context.getUIManager().getOperatorManager().getConfig();
        for (Map.Entry<String, OperatorKeyConfigListStruct<C>> configList : config.operatorKeyConfigs) {
            for (OperatorKeyConfigItemGroupStruct<C> cfg : configList.getValue()) {
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
}
