package com.minechinima.ui.elements;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class UICheckBox<C extends IContext> extends UIValueElement<Boolean, BooleanValue, C> {
    private final String label;
    private final ImBoolean ptr = new ImBoolean();

    public UICheckBox(String label) {
        this.label = label;
    }

    @Override
    protected void drawElement(UIContext<C> context) {
        this.ptr.set(this.value.get());
        if (ImGui.checkbox(context.getTranslationBackpack().getTranslation(this.label + ".title", context.getLocale()), this.ptr)) {
            if (this.ptr.get() != this.value.get()) {
                context.getUIManager().getOperatorManager().writeToTransaction(this.value.controllers().set(this.ptr.get()));
                context.getUIManager().getOperatorManager().commitTransaction("Set checkbox", true);
            }
        }
    }
}
