package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.operator.PropertyModifierGroupStruct;
import com.minechinima.operator.defaults.DragValueSliderProp;

public class RoundModal extends PropertyModifierGroupStruct<DragValueSliderProp> {
    public RoundModal() {
        super("round", "round to nearest integer");
    }

    @Override
    public void onEvent(DragValueSliderProp original, DragValueSliderProp props) {
        props.round.set(!props.round.get());
    }

    @Override
    public void onStopEvent(DragValueSliderProp original, DragValueSliderProp props) {
        props.round.set(false);
    }
}
