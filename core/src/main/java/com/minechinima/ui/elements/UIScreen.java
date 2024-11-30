package com.minechinima.ui.elements;

import com.minechinima.ui.UIContext;
import com.minechinima.rendering.GLUtils;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

public class UIScreen extends UIElement {
    private UISpace panel;

    public UIScreen(UISpace panelBody) {
        this.setPanel(panelBody);
    }

    public void setPanel(UISpace panel) {
        if (this.panel != null) this.panel.onClose();
        this.children.remove(this.panel);
        this.panel = panel;
        this.children.add(panel);
    }

    @Override
    public final void draw(UIContext context) {
        float ar = 1F/this.panel.getDefaultAspectRatio();
        int[] windowSize = GLUtils.getGLFWWindowSize(context.getWindow().ptr());
        float windowHeight = windowSize[1];
        float windowWidth = windowSize[0] / 2F;
        float panelHeight = windowWidth * ar;
        float panelWidth = windowWidth;
        if (panelHeight > windowHeight * 0.75F) {
            float f = panelHeight / (windowHeight * 0.75F);
            panelHeight /= f;
            panelWidth /= f;
        }

        ImGui.setNextWindowSize(panelWidth, panelHeight, ImGuiCond.Appearing);
        int windowFlags = ImGuiWindowFlags.NoCollapse
                | ImGuiWindowFlags.NoNavInputs
                | ImGuiWindowFlags.NoScrollbar
                | ImGuiWindowFlags.MenuBar
                | ImGuiWindowFlags.NoTitleBar;

        if (ImGui.begin("   ##" + this.hashCode(), windowFlags)) {
            if (ImGui.beginMenuBar()) {
                ImGui.setNextItemWidth(ImGui.calcTextSizeX(this.panel.getPanelName()) + ImGui.getStyle().getFramePadding().x * 2);
                if (ImGui.beginCombo("", this.panel.getPanelName(), ImGuiComboFlags.NoArrowButton)) {
                    for (String name : context.getPanelFactory().getRegisteredNames()) {
                        boolean isSelected = name.equals(this.panel.getPanelName());
                        if (ImGui.selectable(name, isSelected)) {
                            this.setPanel(context.getPanelFactory().createUIPanel(name));
                        }
                        if (isSelected) ImGui.setItemDefaultFocus();
                    }
                    ImGui.endCombo();
                }

                this.panel.drawMenuBarItems(context);
                ImGui.endMenuBar();
            }
            this.panel.drawBody(context);
        }
        ImGui.end();
    }
}
