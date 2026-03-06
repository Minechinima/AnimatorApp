package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.config.modal.PropertyModifierCommandConfig;
import com.minechinima.operator.modal.IPropertyModifierCommand;

public abstract class TranslateModalAxis extends PropertyModifierCommandConfig<TransformProps> {
    public enum ConstraintAxis { X, Y, Z }

    private final ConstraintAxis constraintAxis;

    public TranslateModalAxis(ConstraintAxis constraintAxis) {
        super("Axis", "");
        this.constraintAxis = constraintAxis;
    }

    @Override
    public IPropertyModifierCommand<TransformProps, ?> factory() {
        return new IPropertyModifierCommand<>(this) {
            @Override
            public IControllerCommand modify(TransformProps props) {
                switch (this.modifierConfig.constraintAxis) {
                    case X -> {
                        boolean wasOnlyX = props.constraintX.get() && !props.constraintY.get() && !props.constraintZ.get();
                        if (wasOnlyX) {
                            return props.constraintX.controller().set(false);
                        }
                        return props.constraintX.controller().set(true)
                                .next(props.constraintY.controller().set(false))
                                .next(props.constraintZ.controller().set(false));
                    }
                    case Y -> {
                        boolean wasOnlyY = !props.constraintX.get() && props.constraintY.get() && !props.constraintZ.get();
                        if (wasOnlyY) {
                            return props.constraintY.controller().set(false);
                        }
                        return props.constraintX.controller().set(false)
                                .next(props.constraintY.controller().set(true))
                                .next(props.constraintZ.controller().set(false));
                    }
                    case Z -> {
                        boolean wasOnlyZ = !props.constraintX.get() && !props.constraintY.get() && props.constraintZ.get();
                        if (wasOnlyZ) {
                            return props.constraintZ.controller().set(false);
                        }
                        return props.constraintX.controller().set(false)
                                .next(props.constraintY.controller().set(false))
                                .next(props.constraintZ.controller().set(true));
                    }
                }
                return null;
            }
        };
    }
}
