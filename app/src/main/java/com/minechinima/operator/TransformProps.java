package com.minechinima.operator;

import com.minechinima.model.OrientationEnumValueStruct;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.FloatValue;
import com.minechinima.model.values.vectors.Matrix3fValue;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIDragFloat;
import com.minechinima.ui.elements.UIEnum;
import com.minechinima.ui.elements.UIVectorElement;

public class TransformProps extends OperatorProperties {
    @InGui(translationPrefix = "value", as = UIVectorElement.class)
    public final Vector3fValue value = new Vector3fValue("value");
    @InGui(translationPrefix = "snapping", as = UICheckBox.class)
    public final BooleanValue snapping = new BooleanValue("snapping");
    @InGui(translationPrefix = "scale_factor", as = UIDragFloat.class)
    public final FloatValue scaleFactor = new FloatValue("scale_factor", 1.0F);
    @InGui(translationPrefix = "orientation", as = UIEnum.class)
    public final OrientationEnumValueStruct orientation = new OrientationEnumValueStruct("orientation", OrientationEnumValueStruct.Orientation.GLOBAL);
    @InGui(translationPrefix = "constraint_x", as = UICheckBox.class)
    public final BooleanValue constraintX = new BooleanValue("constraint_x");
    @InGui(translationPrefix = "constraint_y", as = UICheckBox.class)
    public final BooleanValue constraintY = new BooleanValue("constraint_y");
    @InGui(translationPrefix = "constraint_z", as = UICheckBox.class)
    public final BooleanValue constraintZ = new BooleanValue("constraint_z");
    // Internal - not shown in UI (no @InGui annotation)
    public final Matrix3fValue orientationMatrix = new Matrix3fValue("orientation_matrix");

    public TransformProps() {
        this.addChild("value", this.value);
        this.addChild("snapping", this.snapping);
        this.addChild("scaleFactor", this.scaleFactor);
        this.addChild("orientation", this.orientation);
        this.addChild("constraintX", this.constraintX);
        this.addChild("constraintY", this.constraintY);
        this.addChild("constraintZ", this.constraintZ);
        this.addChild("orientationMatrix", this.orientationMatrix);
    }
}
