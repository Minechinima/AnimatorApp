package com.minechinima.ui.panels;

import com.minechinima.model.BBContext;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.window.UISpace;
import imgui.ImGui;

import java.util.Comparator;
import java.util.List;

public class UIOutlinerPanel extends UISpace<BBOperatorContext> {
    public UIOutlinerPanel(String panelName) {
        super(panelName);
    }

    @Override
    public void drawMenuBarItems(UIContext context) {

    }

    @Override
    public float getDefaultAspectRatio() {
        return 0.5F;
    }

    @Override
    public void drawBody(UIContext<BBOperatorContext> context) {
        BBDatabase data = ((BBOperatorContext) context.getOperatorContext()).bbContext().database;
        BBContext ctx = ((BBOperatorContext) context.getOperatorContext()).bbContext();

        data.scenes.getChildren().forEach(scene -> {
            if (ImGui.treeNode(scene.getUUID().toString(), scene.getIdentifier())) {
                this.renderObjects(context, ctx, scene.rootCollection.getPointerTargets());
                ImGui.treePop();
            }
        });

        this.renderObjects(context, ctx, List.copyOf(data.objects.getChildren()));
    }

    private void renderObject(UIContext uictx, BBContext ctx, BaseObject obj) {
        String icon = obj.data.get() != null ? obj.data.get().getOutlinerIconUnicode() : BBIconUnicodes.OBJECT_OUTLINE;
        if (ImGui.selectable(icon + " " + obj.getIdentifier(), obj == ctx.activeObject.get())) {
            uictx.getUIManager().getOperatorManager().executeTransaction("select", ctx.activeObject.controller().set(obj), true);
        }
    }

    private void renderObjects(UIContext uictx, BBContext ctx, List<BaseObject> objects) {
        objects.stream().sorted(Comparator.comparing(BaseStructure::getIdentifier))
        .forEach((obj) -> {
            this.renderObject(uictx, ctx, obj);
        });
    }
}
