package com.minechinima.ui.elements;

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

        }
    }
}
