package com.minechinima.operator;

import com.minechinima.model.values.FloatValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIDragFloat;

public class ZoomViewportProps extends OperatorPropertiesGroupStruct {
    @InGui(as = UIDragFloat.class, translationPrefix = "delta")
    public final FloatValue delta = new FloatValue("delta", 1);
    public ZoomViewportProps(String id) {
        super(id);
        this.addChild("delta", this.delta);
    }

    @Override
    public OperatorPropertiesGroupStruct copy() {
        ZoomViewportProps copy = new ZoomViewportProps("");
        copy.copyFrom(this);
        return copy;
    }
}
