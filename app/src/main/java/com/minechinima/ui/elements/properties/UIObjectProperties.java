package com.minechinima.ui.elements.properties;

import com.minechinima.model.app.object.BaseObject;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.elements.BBUIWindow;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIPointerTargetSelector;
import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;

public class UIObjectProperties extends UIPropertyContext<BaseObject> {
    private final UICheckBox viewportHidden = new UICheckBox("viewport visibility");
    private final UICheckBox renderHidden = new UICheckBox("render visibility");
    private final UIPointerTargetSelector<BaseObject> parent = new UIPointerTargetSelector<>("Select Parent");

    public UIObjectProperties() {
        super(BaseObject.class);
    }

    @Override
    public String getIconUnicode() {
        return BBIconUnicodes.OBJECT_OUTLINE;
    }

    @Override
    public int getIndex() {
        return UIPropertyContextIndex.OBJECT;
    }

    @Override
    protected void drawBody(UIContext context) {
        if (ImGui.collapsingHeader("Transformation", ImGuiTreeNodeFlags.DefaultOpen)) {
            drawFromEntryPoint(UIObjectPropertiesTransformation.class, context);
        }

        if (ImGui.collapsingHeader("Visibility")) {
            //this.viewportHidden.init(this.getContext().viewportVisibility).render(context);
            //this.renderHidden.init(this.getContext().renderVisibility).render(context);

            drawFromEntryPoint(UIObjectPropertiesVisibility.class, context);
        }

        if (ImGui.collapsingHeader("Relations")) {
            this.parent.setSourceList(((BBUIWindow)context.getCurrentWindow()).context.database.objects);
            this.parent.init(this.getContext().parent, (obj) -> {
                //context.getApplication().getMainController().executeNewTransactionToHistory("",
                //        () -> ObjectsController.setParent(context.getContextData().data, this.getContext(), obj));
            }).render(context);

            drawFromEntryPoint(UIObjectPropertiesRelations.class, context);
        }
    }

    public interface UIObjectPropertiesTransformation extends IUIHandler<BaseObject> {

    }

    public interface UIObjectPropertiesVisibility extends IUIHandler<BaseObject> {

    }

    public interface UIObjectPropertiesRelations extends IUIHandler<BaseObject> {

    }
}
