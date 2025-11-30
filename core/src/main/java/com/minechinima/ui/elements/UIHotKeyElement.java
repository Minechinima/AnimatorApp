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

public class UIHotKeyElement<C extends IContext> extends UIElement<C> {
    private HotKeyConfigSelectorEnumStruct selector;
    private final UIEnum<HotKeyConfigGroupStruct, HotKeyConfigSelectorEnumStruct, C> hotkey = new UIEnum<>("hotkey");

    private Consumer<UIContext<C>> cachedUpdate;

    public UIHotKeyElement(HotKeyConfigSelectorEnumStruct selector) {
        this.selector = selector;
    }

    private void createUI(UIContext<C> context) {
        this.children.clear();

        this.hotkey.init(this.selector);
        this.children.add(this.hotkey);

        /* get hotkey config ui */
        if (this.selector.get() != null) {
            UIHandlerDummy dummy = new UIHandlerDummy(hotkey.getValue().get(),
                    context.getUiHandlerRegistry().getUnnamedHandlersFor(this.selector.get()));
            this.children.add(dummy);
        }
    }

    @Override
    protected void _onShow(UIContext<C> context) {
        this.selector.getEventBus().subscribe(this);
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
    }
}

