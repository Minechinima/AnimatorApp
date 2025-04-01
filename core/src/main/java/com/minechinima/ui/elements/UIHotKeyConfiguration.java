package com.minechinima.ui.elements;

import com.minechinima.input.hotkey.*;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.OperatorConfig;
import com.minechinima.operator.config.OperatorConfigurations;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIHandler;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.Collection;

public class UIHotKeyConfiguration<C extends IContext> extends UIElement<C> {

    public UIHotKeyConfiguration() {

    }

    @Override
    public void draw(UIContext<C> context) {
        OperatorConfigurations<?> config = context.getUIManager().getOperatorManager().getConfig();
        for (OperatorConfig cfg : config.configs) {
            UIEnum<HotKeyConfig, HotKeyConfigSelectorEnum, C> hotkey = new UIEnum<>("hotkey");
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
            uiptr.setSourceList(cfg.operatorType.getReferenceFrame());
            uiptr.init(cfg.operatorType).draw(context);
        }
    }
}
