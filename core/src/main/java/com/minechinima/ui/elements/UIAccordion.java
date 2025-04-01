package com.minechinima.ui.elements;

import com.minechinima.operator.IContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIElement;
import imgui.ImGui;

import java.util.LinkedList;

public class UIAccordion<C extends IContext> extends UIGroup<C> {
    private final String label;
    private final LinkedList<IUIElement<UIContext<C>>> children = new LinkedList<>();

    public UIAccordion(String label) {
        this.label = label;
    }

    public void addChild(IUIElement<UIContext<C>> element) {
        this.children.add(element);
    }

    @Override
    public void draw(UIContext<C> context) {
        if (ImGui.collapsingHeader(context.getTranslationBackpack().getTranslation(this.label + ".title", context.getLocale()))) {
            for (IUIElement<UIContext<C>> child : this.children) {
                child.draw(context);
            }
        }
    }
}
