package com.minechinima.ui.elements.window;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.defaults.ChangeSpaceTypeProp;
import com.minechinima.ui.UIContext;
import com.minechinima.rendering.GLUtils;
import com.minechinima.ui.elements.UIElement;
import imgui.ImGui;
import imgui.flag.ImGuiComboFlags;
import imgui.flag.ImGuiCond;
import imgui.flag.ImGuiWindowFlags;

/**
 * A window with one panel. A UIScreen belongs to one {@link UIWindow}
 * @param <C>
 */
public class UIScreen<C extends IContext> extends UIElement<C> {
    private UISpace<C> panel;
    //TODO better focus logic
    private boolean focused = false;

    public UIScreen(UISpace<C> panelBody) {
        this.setPanel(panelBody);
    }

    public void setPanel(UISpace<C> panel) {
        if (this.panel != null) this.panel.onClose();
        this.children.remove(this.panel);
        this.panel = panel;
        this.children.add(panel);
    }

    public UISpace<C> getPanel() {
        return panel;
    }


    @Override
    public final void draw(UIContext<C> context) {
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
            if (ImGui.isWindowFocused() || this.focused) {
                this.focused = true;
                context.getContextBuilder().setClickedUIScreen(this);
            }

            if (ImGui.isWindowHovered()) {
                context.getContextBuilder().setCurrentHoveredUISpace(this.panel);
            }

            if (ImGui.beginMenuBar()) {
                ImGui.setNextItemWidth(ImGui.calcTextSizeX(this.panel.getPanelName()) + ImGui.getStyle().getFramePadding().x * 2);
                if (ImGui.beginCombo("", this.panel.getPanelName(), ImGuiComboFlags.NoArrowButton)) {
                    for (String name : context.getPanelFactory().getRegisteredNames()) {
                        boolean isSelected = name.equals(this.panel.getPanelName());
                        if (ImGui.selectable(name, isSelected)) {
                            OperatorManager operatorManager = context.getUIManager().getOperatorManager();
                            operatorManager.execute(operatorManager.getConfig().operatorTypeRegistry.changeSpaceType, new ChangeSpaceTypeProp(context.getPanelFactory().createUIPanel(name)));
                            //this.setPanel(context.getPanelFactory().createUIPanel(name));
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
