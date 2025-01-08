package com.minechinima.ui.elements;

import com.minechinima.i18n.TranslationBackpack;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.LinkedList;
import java.util.Locale;

public class UIAccordion extends UIGroup {
    private final String label;
    private final LinkedList<UIElement> children = new LinkedList<>();

    public UIAccordion(String label) {
        this.label = label;
    }

    public void addChild(UIElement element) {
        this.children.add(element);
    }

    @Override
    protected void draw(UIContext context) {
        if (ImGui.collapsingHeader(context.getRegisteredObj(TranslationBackpack.class).getTranslation(this.label + ".title", Locale.getDefault()))) {
            for (UIElement child : this.children) {
                child.draw(context);
            }
        }
    }
}
