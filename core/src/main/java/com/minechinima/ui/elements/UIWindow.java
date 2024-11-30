package com.minechinima.ui.elements;

import com.minechinima.ui.UIContext;
import imgui.ImGui;

import java.util.ArrayList;
import java.util.List;

//TODO windows is too specific, maybe into app?
public class UIWindow extends UIElement {
    public final List<UIScreen> screens = new ArrayList<>();

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
