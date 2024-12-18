package com.minechinima.ui.elements;

import com.minechinima.operator.OperatorContext;
import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

public class UIWindow<C extends OperatorContext> extends UIElement {
    public final List<UIScreen> screens = new ArrayList<>();
    public final C context;

    public UIWindow(C context) {
        this.context = context;
    }

    @Override
    protected void draw(UIContext context) {
        if (ImGui.beginMainMenuBar()) {
            ImGui.text("BB DEV");
            ImGui.menuItem("Edit");

            if (ImGui.beginMenu("Window")) {
                if (ImGui.beginMenu("New Window")) {
                    for (String panelName : context.getPanelFactory().getRegisteredNames()) {
                        if (ImGui.menuItem(panelName)) {
                            this.screens.add(new UIScreen(context.getPanelFactory().createUIPanel(panelName)));
                        }
                    }
                    ImGui.endMenu();
                }
                ImGui.endMenu();
            }

            ImGui.menuItem("Help");
        }
        ImGui.endMainMenuBar();

        for (UIScreen screen : this.screens) {
            screen.draw(context);
        }
    }
}
