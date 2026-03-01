package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

/**
 * Precision mode modifier for transform operators.
 * When held (e.g. Shift), reduces the drag sensitivity by {@link #PRECISION_FACTOR},
 * allowing finer control over the transformation value.
 */
public class TransformModalPrecision extends PropertyModifierReleaseableConfig<TransformProps> {
    public static final float PRECISION_FACTOR = 0.1f;

    public TransformModalPrecision() {
        super("precision", "Precision mode");
    }

    @Override
    public IPropertyModifierReleasableCommand<TransformProps, ?> factory() {
        return new IPropertyModifierReleasableCommand<>(this) {
            private float originalPrecision;

            @Override
            public IControllerCommand modify(TransformProps props) {
                this.originalPrecision = props.precision.get();
                return props.precision.controller().set(PRECISION_FACTOR);
            }

            @Override
            public IControllerCommand onReleaseEvent(TransformProps props) {
                return props.precision.controller().set(this.originalPrecision);
            }
        };
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return true;
    }
}
