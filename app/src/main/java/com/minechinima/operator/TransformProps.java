package com.minechinima.operator;

import com.minechinima.model.AxisConstraintEnumValueStruct;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIDragFloat;
import com.minechinima.ui.elements.UIEnum;
import com.minechinima.ui.elements.UIVectorElement;

public class TransformProps extends OperatorProperties {
    @InGui(translationPrefix = "value", as = UIVectorElement.class)
    public final Vector3fValue value = new Vector3fValue("value");
    @InGui(translationPrefix = "local", as = UICheckBox.class)
    public final BooleanValue local = new BooleanValue("local");
    @InGui(translationPrefix = "axis_constraint", as = UIEnum.class)
    public final AxisConstraintEnumValueStruct axisConstraint = new AxisConstraintEnumValueStruct("axis_constraint");
    @InGui(translationPrefix = "precision", as = UIDragFloat.class)
    public final FloatValue precision = new FloatValue("precision", 1.0f);
    @InGui(translationPrefix = "snap", as = UICheckBox.class)
    public final BooleanValue snap = new BooleanValue("snap");

    public TransformProps() {
        this.addChild("value", this.value);
        this.addChild("local", this.local);
        this.addChild("axis_constraint", this.axisConstraint);
        this.addChild("precision", this.precision);
        this.addChild("snap", this.snap);
    }
}
