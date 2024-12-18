package com.minechinima.ui.elements;

import com.minechinima.controller.Controller;
import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.ui.UIContext;
import imgui.ImGui;
import imgui.type.ImBoolean;

import java.util.Locale;

public class UICheckBox extends UIValueElement<Boolean, BooleanValue> {
    private final String label;
    private final ImBoolean ptr = new ImBoolean();

    public UICheckBox(String label) {
        this.label = label;
    }

    @Override
    protected void drawElement(UIContext context) {
        this.ptr.set(this.value.get());
        if (ImGui.checkbox(context.getTranslationBackpack().getTranslation(this.label + ".title", context.getLocale()), this.ptr)) {
            if (this.ptr.get() != this.value.get()) {

            }
        }
    }
}
