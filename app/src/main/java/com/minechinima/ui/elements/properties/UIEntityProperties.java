package com.minechinima.ui.elements.properties;

import com.minechinima.model.app.object.EntityType;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;

public class UIEntityProperties extends UIPropertyContext<EntityType> {
    public UIEntityProperties() {
        super(EntityType.class);
    }

    @Override
    public int getIndex() {
        return UIPropertyContextIndex.ENTITY;
    }

    @Override
    protected void drawBody(UIContext context) {

    }

    @Override
    public String getIconUnicode() {
        return BBIconUnicodes.OBJECT_OUTLINE;
    }
}
