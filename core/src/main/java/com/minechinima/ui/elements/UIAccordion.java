package com.minechinima.ui.elements;

import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.LinkedList;

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
        if (ImGui.collapsingHeader(context.getTranslationBackpack().getTranslation(this.label + ".title", context.getLocale()))) {
            for (UIElement child : this.children) {
                child.draw(context);
            }
        }
    }
}
