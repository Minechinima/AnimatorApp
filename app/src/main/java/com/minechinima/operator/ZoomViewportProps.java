package com.minechinima.operator;

import com.minechinima.model.values.FloatValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UIDragFloat;

public class ZoomViewportProps extends OperatorProperties {
    @InGui(as = UIDragFloat.class, translationPrefix = "delta")
    public final FloatValue delta = new FloatValue("delta", 1);
    public ZoomViewportProps() {
        this.addChild("delta", this.delta);
    }
}
