package com.minechinima.ui.elements;

import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigSelectorEnumStruct;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ChangeEvent;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

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

        this.children.add(this.hotKeyElement);

        this.uiptr.setSourceList((BaseValueHierarchy<OperatorType>) this.config.operatorType.getReferenceFrame());
        this.uiptr.init(this.config.operatorType);
        this.children.add(this.uiptr);

        /* operator properties */
        if (this.config.props.getProps() != null) {
            UIHandlerDummy dummy = new UIHandlerDummy(this.config.props.getProps(),
                    context.getUiHandlerRegistry().getUnnamedHandlersFor(this.config.props.getProps()));
            this.children.add(dummy);
        }

        /* modal hotkey confirm */
        if (this.config.modalConfirmConfig.get() != null) {
            UICheckBox<C> checkBox = new UICheckBox<>("confirm on Release");
            checkBox.init(this.config.modalConfirmConfig.get().confirmOnRelease);

            this.children.add(checkBox);
            this.children.add(new UIHotKeyElement<>(this.config.modalConfirmConfig.get().hotKeyConfigSelector));
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
