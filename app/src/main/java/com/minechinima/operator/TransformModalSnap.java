package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

/**
 * Snap modifier for transform operators.
 * When held (e.g. Ctrl), enables snapping which rounds the transformation
 * value to the nearest integer.
 */
public class TransformModalSnap extends PropertyModifierReleaseableConfig<TransformProps> {
    public TransformModalSnap() {
        super("snap", "Snap to grid");
    }

    @Override
    public IPropertyModifierReleasableCommand<TransformProps, ?> factory() {
        return new IPropertyModifierReleasableCommand<>(this) {
            @Override
            public IControllerCommand modify(TransformProps props) {
                return props.snap.controller().set(true);
            }

            @Override
            public IControllerCommand onReleaseEvent(TransformProps props) {
                return props.snap.controller().set(false);
            }
        };
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return true;
    }
}
