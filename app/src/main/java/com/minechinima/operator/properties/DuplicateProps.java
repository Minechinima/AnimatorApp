package com.minechinima.operator.properties;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;

public class DuplicateProps extends OperatorProperties {
    @InGui(as = UICheckBox.class, translationPrefix = "linked")
    public final BooleanValue linked = new BooleanValue("linked", false);

    public DuplicateProps() {
        this.addChild("linked", this.linked);
    }
}
