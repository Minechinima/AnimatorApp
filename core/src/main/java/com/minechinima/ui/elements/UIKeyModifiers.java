package com.minechinima.ui.elements;

import com.minechinima.input.hotkey.configs.KeyModifiers;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;

public class UIKeyModifiers<C extends IContext> extends UIElement<C> {
    private KeyModifiers value;

    public UIKeyModifiers(String title) {

    }

    public UIKeyModifiers<C> init(KeyModifiers value) {
        this.value = value;
        return this;
    }

    @Override
    public void draw(UIContext<C> context) {
        context.getUiHandlerRegistry().getUnnamedHandlersFor(this.value).forEach(handler -> handler.draw(context, this.value));
    }
}
