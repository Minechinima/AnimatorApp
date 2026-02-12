package com.minechinima.ui.elements;

import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.ChangeEvent;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.config.operator.OperatorMacroPropertiesConfig;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.Collection;
import java.util.function.Consumer;

public class UIOperatorConfigElement<C extends IContext> extends UIElement<C> {
    private OperatorConfigGroupStruct<C> config;
    private final UIHotKeyElement<C> hotKeyElement;
    private final UIPointerTargetSelector<OperatorType, C> uiptr = new UIPointerTargetSelector<>("OperatorType");

    private Consumer<UIContext<C>> cachedUpdate;

    public UIOperatorConfigElement(OperatorConfigGroupStruct<C> hotKeyConfig) {
        this.config = hotKeyConfig;
        this.hotKeyElement = new UIHotKeyElement<>(this.config.hotKeyConfigSelector);
    }

    private void createUI(UIContext<C> context) {
        this.children.clear();

        this.uiptr.setSourceList((BaseValueHierarchy<OperatorType>) this.config.operatorType.getReferenceFrame());
        this.uiptr.init(this.config.operatorType);

        this.children.add(this.uiptr);
        this.children.add(this.hotKeyElement);

        /* operator properties */
        if (this.config.props.get() != null) {
            UIHandlerDummy dummy = new UIHandlerDummy(this.config.props.get(),
                    context.getUiHandlerRegistry().getUnnamedHandlersFor(this.config.props.get()));
            this.children.add(dummy);
        }

        if (this.config.macroPropertiesConfig.get() != null) {
            for (OperatorMacroPropertiesConfig.MacroConfig propertyConfig : this.config.macroPropertiesConfig.get().getMacroProperties().values()) {
                this.createMacroPropConfigs(context, propertyConfig);
            }
        }

        /* modal hotkey confirm */
        if (this.config.modalConfirmConfig.get() != null) {
            UICheckBox<C> checkBox = new UICheckBox<>("confirm on Release");
            checkBox.init(this.config.modalConfirmConfig.get().confirmOnRelease);

            this.children.add(checkBox);
            this.children.add(new UIHotKeyElement<>(this.config.modalConfirmConfig.get().hotKeyConfigSelector));
        }
    }

    protected void createMacroPropConfigs(UIContext<C> context, OperatorMacroPropertiesConfig.MacroConfig propertyConfig) {
        UIHandlerDummy dummy = new UIHandlerDummy(propertyConfig.props,
                context.getUiHandlerRegistry().getUnnamedHandlersFor(propertyConfig.props));

        this.children.add(dummy);

        if (propertyConfig instanceof OperatorMacroPropertiesConfig.ModalMacroConfig config) {
            UICheckBox checkBox = new UICheckBox<>("modal_abort");
            checkBox.init(config.cancelMacros);
            this.children.add(checkBox);
        }

        Collection<OperatorMacroPropertiesConfig.MacroConfig> propConfigs = propertyConfig.getMacroProperties().values();
        for (OperatorMacroPropertiesConfig.MacroConfig macroCfg : propConfigs) {
            this.createMacroPropConfigs(context, macroCfg);
        }
    }

    @Override
    protected void _onShow(UIContext<C> context) {
        this.config.getEventBus().subscribe(this);
        this.createUI(context);
    }

    @SubscribeEvent(type = SubscribeEvent.Type.ALL)
    private void onEvent(ChangeEvent event) {
        this.cachedUpdate = this::createUI;
    }


    @Override
    public void draw(UIContext<C> context) {
        if (this.cachedUpdate != null) {
            this.cachedUpdate.accept(context);
            this.cachedUpdate = null;
        }
        ImGui.separator();
    }
}
