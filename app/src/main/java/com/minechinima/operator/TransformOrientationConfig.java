package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.OrientationEnumValueStruct;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

public class TransformOrientationConfig extends PropertyModifierCommandConfig<TransformProps> {
    public TransformOrientationConfig() {
        super("transform_orientation", "transform_orientation");
    }

    @Override
    public IPropertyModifierCommand<TransformProps, ?> factory() {
        return new IPropertyModifierCommand<>(this) {
            @Override
            public IControllerCommand modify(TransformProps props) {
                if (props.orientation.get() == OrientationEnumValueStruct.Orientation.GLOBAL) {
                    props.orientation.controller().set(OrientationEnumValueStruct.Orientation.LOCAL);
                } else if (props.orientation.get() == OrientationEnumValueStruct.Orientation.LOCAL) {
                    props.orientation.controller().set(OrientationEnumValueStruct.Orientation.GLOBAL);
                }

                return props.orientation.controller().set(OrientationEnumValueStruct.Orientation.GLOBAL);
            }
        };
    }
}
