package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.AxisEnumValueStruct;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

public abstract class TranslateModalAxis extends PropertyModifierCommandConfig<TransformProps> {
    private final AxisEnumValueStruct.Axis axisToSet;

    public TranslateModalAxis(AxisEnumValueStruct.Axis axisToSet) {
        super("Axis", "");
        this.axisToSet = axisToSet;
    }

    @Override
    public IPropertyModifierCommand<TransformProps, ?> factory() {
        return new IPropertyModifierCommand<>(this) {
            @Override
            public IControllerCommand modify(TransformProps props) {
                if (props.axis.get() == this.modifierConfig.axisToSet) {
                    return props.transformOnAxis.controller().set(!props.transformOnAxis.get());
                }

                return props.transformOnAxis.controller().set(true)
                        .next(props.axis.controller().set(this.modifierConfig.axisToSet));
            }
        };
    }
}
