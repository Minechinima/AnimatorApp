package com.minechinima.model.app.object;

import com.minechinima.model.values.FloatValue;
import com.minechinima.ui.BBIconUnicodes;

public class CameraType extends ObjectType {
    public final FloatValue fov = new FloatValue("fov", 90F);

    public CameraType() {
        this.addChild("fov", this.fov);
    }

    @Override
    public String getOutlinerIconUnicode() {
        return BBIconUnicodes.CAMERA_SOLID;
    }
}
