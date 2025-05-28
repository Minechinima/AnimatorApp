package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.PropertyModifierGroupStruct;
import com.minechinima.operator.defaults.DragValueSliderProp;

public class DragIncrementModal extends PropertyModifierGroupStruct<DragValueSliderProp> {
    public final FloatValue incrementMultiplier;

    public DragIncrementModal() {
        this(0.01F);
    }

    public DragIncrementModal(float incrementMultiplier) {
        super("drag_increment", "description");
        this.addChild("increment_multiplier", this.incrementMultiplier = new FloatValue("increment_multiplier", incrementMultiplier));
    }

    @Override
    public void onEvent(DragValueSliderProp original, DragValueSliderProp props) {
        if (props.increment.get().equals(this.incrementMultiplier.get())) {
            props.increment.set(original.increment.get());
            return;
        }

        props.increment.set(this.incrementMultiplier.get());
    }

    @Override
    public void onStopEvent(DragValueSliderProp original, DragValueSliderProp props) {
        props.increment.set(original.increment.get());
    }
}
