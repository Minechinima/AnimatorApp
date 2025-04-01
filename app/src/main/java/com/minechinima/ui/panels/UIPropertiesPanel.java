package com.minechinima.ui.panels;

import com.minechinima.model.BBContext;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UIHotKeyConfiguration;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.properties.IUIPropertyContext;
import com.minechinima.ui.elements.properties.PropertyContextRegistry;
import imgui.ImColor;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class UIPropertiesPanel extends UISpace {
    private IUIPropertyContext currentContext;
    private UIHotKeyConfiguration test = new UIHotKeyConfiguration();

    public UIPropertiesPanel(String windowTitle) {
        super(windowTitle);
    }

    @Override
    public float getDefaultAspectRatio() {
        return 0.5F;
    }

    @Override
    public void drawMenuBarItems(UIContext context) {

    }

    @Override
    public void drawBody(UIContext context) {
        PropertyContextRegistry registry = (PropertyContextRegistry) context.getRegisteredObj(PropertyContextRegistry.class);
        //TODO pass the C context type to everything in the UI?? Context tpye not very safe atm... raw types
        BBContext uiContext = ((BBOperatorContext) context.getOperatorContext()).bbContext();
        BaseObject active;
        Set<IUIPropertyContext> bodies = registry.getStandardBodies();

        if ((active = uiContext.activeObject.getReference()) != null) {
            bodies.addAll(registry.getReadyToShowContexts(active));
            bodies.addAll(registry.getReadyToShowContexts(active.data.get()));
        }

        if (bodies.isEmpty()) return;

        List<IUIPropertyContext> sorted = new ArrayList<>(bodies);
        sorted.sort(Comparator.comparingInt(IUIPropertyContext::getIndex));

        this.updateCurrentContext(sorted);
        this.drawSideBar(sorted);

        ImGui.sameLine();
        ImGui.beginChild("body");
        if (this.currentContext != null) this.currentContext.render(context);

        test.render(context);


        ImGui.endChild();
    }

    protected void updateCurrentContext(List<IUIPropertyContext> bodies) {
        if (this.currentContext == null) {
            this.currentContext = bodies.get(0);
        } else if (!bodies.contains(this.currentContext)) {
            int lastIndex = this.currentContext.getIndex();
            this.currentContext = null;

            for (int i = 0; i < bodies.size(); i++) {
                /* the last index is smaller than the smalled available index -> choose that context*/
                if (i == 0 && lastIndex <= bodies.get(i).getIndex()) {
                    this.currentContext = bodies.get(i);
                    break;
                } else if (i > 0 && bodies.get(i - 1).getIndex() <= lastIndex && lastIndex <= bodies.get(i).getIndex()) {
                    /* find two property contexts where the last index is in between to choose the smaller index */
                    this.currentContext = bodies.get(i - 1);
                    break;
                } else if (i == bodies.size() -1 && lastIndex >= bodies.get(i).getIndex()) {
                    this.currentContext = bodies.get(i);
                }
            }
            /* safety fallback */
            if (this.currentContext == null) this.currentContext = bodies.get(0);
        }
    }

    protected void drawSideBar(List<IUIPropertyContext> bodies) {
        float maxIconWidth = 0F;
        for (IUIPropertyContext ctx : bodies) {
            float width = ImGui.calcTextSizeX(ctx.getIconUnicode());
            if (width > maxIconWidth) maxIconWidth = width;
        }

        float sidebarPaddingX = 4F;
        float sidebarPaddingY = 2F;
        float sideBarWidth = maxIconWidth + 2 * sidebarPaddingX;
        ImGui.beginChild("sidebar", sideBarWidth, ImGui.getContentRegionAvailY(), false, ImGuiWindowFlags.NoScrollbar);
        ImGui.setCursorPosY(sidebarPaddingY);
        for (IUIPropertyContext ctx : bodies) {
            ImGui.setCursorPosX(sidebarPaddingX + (maxIconWidth - ImGui.calcTextSizeX(ctx.getIconUnicode())) * 0.5F);
            ImGui.text(ctx.getIconUnicode());

            if (ImGui.isItemClicked()) this.currentContext = ctx;
        }

        ImGui.endChild();
        ImVec2 p_min = ImGui.getItemRectMin();
        ImVec2 p_max = ImGui.getItemRectMax();
        ImGui.getWindowDrawList().addLine(p_max.x, p_min.y, p_max.x, p_max.y, ImColor.rgb(1F, 1F, 1F), 1F);
    }
}
