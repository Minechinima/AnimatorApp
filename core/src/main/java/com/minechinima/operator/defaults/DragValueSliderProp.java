package com.minechinima.operator.defaults;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIDragFloat;

public class DragValueSliderProp extends OperatorProperties {
    @InGui(as = UIDragFloat.class, translationPrefix = "increment")
    public final FloatValue increment = new FloatValue("increment", 1F);
    @InGui(as = UICheckBox.class, translationPrefix = "increment")
    public final BooleanValue round = new BooleanValue("round", false);

    public DragValueSliderProp() {
        this.addChild("increment", this.increment);
        this.addChild("round", this.round);
    }
}
