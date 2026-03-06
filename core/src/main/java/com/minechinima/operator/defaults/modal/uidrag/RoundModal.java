package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.config.modal.PropertyModifierReleasableCommandConfig;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

public class RoundModal extends PropertyModifierReleasableCommandConfig<DragValueSliderProp> {
    public RoundModal() {
        super("round", "round to nearest integer");
    }

    @Override
    public IPropertyModifierReleasableCommand<DragValueSliderProp, ?> factory() {
        return new IPropertyModifierReleasableCommand<>(this) {
            @Override
            public IControllerCommand modify(DragValueSliderProp props) {
                return props.round.controller().set(!props.round.get());
            }

            @Override
            public IControllerCommand onReleaseEvent(DragValueSliderProp props) {
                return props.round.controller().set(false);
            }
        };
    }

    @Override
    public boolean useReleaseEvent() {
        return false;
    }
}
