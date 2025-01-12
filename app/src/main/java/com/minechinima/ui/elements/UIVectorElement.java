package com.minechinima.ui.elements;

import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.UIContext;
import imgui.internal.ImGui;
import org.joml.Vector3f;

public class UIVectorElement extends UIValueElement<Vector3f, Vector3fValue> {
    private final UIDragFloat x = new UIDragFloat("X");
    private final UIDragFloat y = new UIDragFloat("Y");
    private final UIDragFloat z = new UIDragFloat("Z");
    private final String title;

    public UIVectorElement(String title) {
        this.title = title;
    }

    @Override
    protected void drawElement(UIContext context) {
        ImGui.text(context.getTranslationBackpack().getTranslation(this.title + ".title", context.getLocale()));
        this.x.init(this.value.x).render(context);
        this.y.init(this.value.y).render(context);
        this.z.init(this.value.z).render(context);
    }
}
