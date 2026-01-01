package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class RoundModal extends PropertyModifierReleaseableConfig<DragValueSliderProp> {
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
    public boolean confirmOnReleaseDefault() {
        return false;
    }
}
