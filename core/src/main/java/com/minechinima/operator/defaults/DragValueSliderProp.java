package com.minechinima.operator.defaults;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.OperatorProperties;

public class DragValueSliderProp extends OperatorProperties {
    public final FloatValue incrementCTRL = new FloatValue("increments_ctrl", 1F);
    public final FloatValue incrementSHIFT = new FloatValue("increments_shift", 0.001F);

    public DragValueSliderProp(String id) {
        super(id);
        this.addChild("increments_ctrl", this.incrementCTRL);
        this.addChild("increments_shift", this.incrementSHIFT);
    }
}
