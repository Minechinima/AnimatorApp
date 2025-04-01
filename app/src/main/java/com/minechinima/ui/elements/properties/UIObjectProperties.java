package com.minechinima.ui.elements.properties;

import com.minechinima.model.app.object.BaseObject;
import com.minechinima.operator.BBOperatorContext;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIPointerTargetSelector;
import imgui.ImGui;

public class UIObjectProperties extends UIPropertyContext<BaseObject> {
    private final UICheckBox viewportHidden = new UICheckBox("viewport visibility");
    private final UICheckBox renderHidden = new UICheckBox("render visibility");
    private final UIPointerTargetSelector<BaseObject, BBOperatorContext> parent = new UIPointerTargetSelector<>("Select Parent");

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
        drawFromEntryPoint(UIObjectPropertiesHandler.class, context);
        drawFromObject(context);

        if (ImGui.collapsingHeader("Relations")) {
            //this.parent.setSourceList(((BBUIWindow)context.getCurrentWindow()).context.database.objects);
            //this.parent.init(this.getContext().parent, (obj) -> {
            //    //context.getApplication().getMainController().executeNewTransactionToHistory("",
            //    //        () -> ObjectsController.setParent(context.getContextData().data, this.getContext(), obj));
            //}).render(context);
        }
    }

    public interface UIObjectPropertiesHandler extends IUIHandler<BaseObject> {}
}
