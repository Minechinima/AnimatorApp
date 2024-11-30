package com.minechinima.ui.elements.properties;

import com.minechinima.model.app.object.CameraType;
import com.minechinima.ui.BBIconUnicodes;
import com.minechinima.ui.UIContext;
import com.minechinima.ui.elements.UIDragFloat;

public class UICameraProperties extends UIPropertyContext<CameraType> {
    private final UIDragFloat fov = new UIDragFloat("fov");

    public UICameraProperties() {
        super(CameraType.class);
    }

    @Override
    public String getIconUnicode() {
        return BBIconUnicodes.CAMERA_OUTLINE;
    }

    @Override
    public int getIndex() {
        return UIPropertyContextIndex.CAMERA;
    }

    @Override
    protected void drawBody(UIContext context) {
        this.fov.init(this.getContext().fov).render(context);
    }
}
