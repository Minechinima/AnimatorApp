package com.minechinima.ui.elements;

import com.minechinima.controller.Controller;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.type.ImBoolean;

public class UICheckBox extends UIValueElement<Boolean, BooleanValue> {
    private final String label;
    private final ImBoolean ptr = new ImBoolean();

    public UICheckBox(String label) {
        this.label = label;
    }

    @Override
    protected void drawElement(UIContext context) {
        this.ptr.set(this.value.get());
        if (ImGui.checkbox(this.label, this.ptr)) {
            if (this.ptr.get() != this.value.get()) {
                Controller controller = context.getWindowManager().getController();
                if (controller.isActive()) {
                    controller.continueTransaction(() -> this.value.set(this.ptr.get()));
                } else {
                    controller.executeNewTransaction(context.getWindowManager().getHistoryManager(),
                            "", () -> this.value.set(this.ptr.get()));
                }
            }
        }
    }
}
