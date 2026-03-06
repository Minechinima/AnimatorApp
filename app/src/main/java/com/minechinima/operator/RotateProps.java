package com.minechinima.operator;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;

public class RotateProps extends TransformProps {
    @InGui(translationPrefix = "trackball", as = UICheckBox.class)
    public final BooleanValue trackball = new BooleanValue("trackball");

    public RotateProps() {
        this.addChild("trackball", this.trackball);
    }
}
