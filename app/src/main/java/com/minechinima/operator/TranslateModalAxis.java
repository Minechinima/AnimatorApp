package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.AxisConstraintEnumValueStruct;
import com.minechinima.model.AxisConstraintEnumValueStruct.AxisConstraint;
import com.minechinima.operator.config.modal.PropertyModifierConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

public abstract class TranslateModalAxis extends PropertyModifierConfig<TransformProps> {
    private final AxisConstraint constraintToSet;

    public TranslateModalAxis(AxisConstraint constraintToSet) {
        super("Axis", "");
        this.constraintToSet = constraintToSet;
    }

    @Override
    public IPropertyModifierCommand<TransformProps, ?> factory() {
        return new IPropertyModifierCommand<>(this) {
            @Override
            public IControllerCommand modify(TransformProps props) {
                if (props.axisConstraint.get() == this.modifierConfig.constraintToSet) {
                    return props.axisConstraint.controller().set(AxisConstraint.ALL);
                }
                return props.axisConstraint.controller().set(this.modifierConfig.constraintToSet);
            }
        };
    }
}
