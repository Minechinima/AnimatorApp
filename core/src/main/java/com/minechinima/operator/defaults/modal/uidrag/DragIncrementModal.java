package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.operator.config.modal.PropertyModifierReleasableCommandConfig;

public class DragIncrementModal extends PropertyModifierReleasableCommandConfig<DragValueSliderProp> {
    public final FloatValue incrementMultiplier;

    public DragIncrementModal() {
        this(0.01F);
    }

    public DragIncrementModal(float incrementMultiplier) {
        super("drag_increment", "description");
        this.addChild("increment_multiplier", this.incrementMultiplier = new FloatValue("increment_multiplier", incrementMultiplier));
    }

    @Override
    public IPropertyModifierReleasableCommand<DragValueSliderProp, DragIncrementModal> factory() {
        return new IPropertyModifierReleasableCommand<>(this) {
            private float originalIncrement;

            @Override
            public IControllerCommand modify(DragValueSliderProp props) {
                this.originalIncrement = props.increment.get();
                return props.increment.controller().set(DragIncrementModal.this.incrementMultiplier.get());
            }

            @Override
            public IControllerCommand onReleaseEvent(DragValueSliderProp props) {
                return props.increment.controller().set(this.originalIncrement);
            }
        };
    }

    @Override
    public boolean useReleaseEvent() {
        return true;
    }
}
