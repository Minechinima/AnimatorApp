package com.minechinima.ui.elements.properties;

import com.minechinima.model.app.object.BaseObject;
import com.minechinima.ui.UIContext;
import org.jetbrains.annotations.NotNull;

public class UIConstraints extends UIPropertyContext<BaseObject> {
    public UIConstraints() {
        super(BaseObject.class);
    }

    @Override
    public int getIndex() {
        return 0;
    }

    @Override
    protected void drawBody(UIContext context) {

    }

    @NotNull
    @Override
    public String getIconUnicode() {
        return "A";
    }
}
