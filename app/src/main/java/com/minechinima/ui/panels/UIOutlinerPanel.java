package com.minechinima.ui.panels;

import com.minechinima.model.BBContext;
import com.minechinima.model.app.BBDatabase;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UISpace;
import com.minechinima.ui.elements.BBUIWindow;
import imgui.ImGui;

import java.util.Comparator;
import java.util.List;

public class UIOutlinerPanel extends UISpace {
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
    public void drawBody(UIContext context) {
        BBDatabase data = ((BBUIWindow)context.getCurrentWindow()).context.database;
        BBContext ctx = ((BBUIWindow)context.getCurrentWindow()).context;

        data.scenes.getChildren().forEach(scene -> {
            if (ImGui.treeNode(scene.getUUID().toString(), scene.getIdentifier())) {
                this.renderObjects(ctx, scene.rootCollection.getPointerTargets());
                ImGui.treePop();
            }
        });

        this.renderObjects(ctx, List.copyOf(data.objects.getChildren()));
    }

    private void renderObject(BBContext ctx, BaseObject obj) {
        String icon = obj.data.get() != null ? obj.data.get().getOutlinerIconUnicode() : BBIconUnicodes.OBJECT_OUTLINE;
        if (ImGui.selectable(icon + " " + obj.getIdentifier(), obj == ctx.activeObject.get())) {
            ctx.activeObject.set(obj);
            //ControllerResult result = context.getApplication().getMainController().executeNewTransactionToHistory("",
            //        () -> BaseValueController.setValue(ctx.activeObject, obj));
        }
    }

    private void renderObjects(BBContext ctx, List<BaseObject> objects) {
        objects.stream().sorted(Comparator.comparing(BaseStructure::getIdentifier))
        .forEach((obj) -> {
            this.renderObject(ctx, obj);
        });
    }
}
