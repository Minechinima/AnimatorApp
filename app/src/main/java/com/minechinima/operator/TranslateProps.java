package com.minechinima.operator;

import com.minechinima.model.AxisEnumValueStruct;
import com.minechinima.model.values.BooleanValue;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.ui.annotation.InGui;
import com.minechinima.ui.elements.UICheckBox;
import com.minechinima.ui.elements.UIEnum;
import com.minechinima.ui.elements.UIVectorElement;

public class TranslateProps extends OperatorPropertiesGroupStruct {
    @InGui(translationPrefix = "value", as= UIVectorElement.class)
    public final Vector3fValue value = new Vector3fValue("value");
    @InGui(translationPrefix = "local", as= UICheckBox.class)
    public final BooleanValue local = new BooleanValue("local");
    @InGui(translationPrefix = "transform_on_axis", as= UICheckBox.class)
    public final BooleanValue transformOnAxis = new BooleanValue("transform_on_axis");
    @InGui(translationPrefix = "axis", as= UIEnum.class)
    public final AxisEnumValueStruct axis = new AxisEnumValueStruct("axis", AxisEnumValueStruct.Axis.X);

    public TranslateProps() {
        this.addChild("value", this.value);
        this.addChild("local", this.local);
        this.addChild("transformOnAxis", this.transformOnAxis);
        this.addChild("axis", this.axis);
    }

    @Override
    public OperatorPropertiesGroupStruct copy() {
        OperatorPropertiesGroupStruct copy = new TranslateProps();
        copy.fromData(this.toData());
        return copy;
    }
}
