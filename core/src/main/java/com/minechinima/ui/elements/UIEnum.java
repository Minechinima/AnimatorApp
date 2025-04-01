package com.minechinima.ui.elements;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.values.IEnumValue;
import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.utils.ILangKeyProvider;
import imgui.ImGui;
import imgui.type.ImInt;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;

public class UIEnum<T extends ILangKeyProvider, J extends BaseStructure & IEnumValue<T>, C extends IContext> extends UIValueElement<T, J, C> {
    private final String title;
    private final ImInt ptr = new ImInt();

    public UIEnum(String title) {
        this.title = title;
    }

    @Override
    protected void drawElement(UIContext<C> context) {
        Map<Integer, T> availableValues = this.value.getAvailableValues();
        T selected = availableValues.get(this.value.getCurrentValue());
        String selectedTitle = selected == null ? "None" : selected.title();

        if (ImGui.beginCombo(this.title, selectedTitle)) {
            for (Map.Entry<Integer, T> entry : availableValues.entrySet()) {
                boolean isSelected = entry.getKey().equals(this.value.getCurrentValue());
                String title = entry.getValue() == null ? "None" : entry.getValue().title();

                if (ImGui.selectable(title, isSelected)) {
                    context.getUIManager().getOperatorManager().executeTransaction("",
                            this.value.controllers().set(entry.getValue()), true);
                }

                if (isSelected) {
                    ImGui.setItemDefaultFocus();
                }
            }
            ImGui.endCombo();
        }
    }
}
