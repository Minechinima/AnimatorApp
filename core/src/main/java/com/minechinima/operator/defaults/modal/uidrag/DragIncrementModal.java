package com.minechinima.operator.defaults.modal.uidrag;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.model.values.FloatValue;
import com.minechinima.operator.modal.IPropertyModifierReleasableCommand;
import com.minechinima.operator.defaults.DragValueSliderProp;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class DragIncrementModal extends PropertyModifierReleaseableConfig<DragValueSliderProp> {
    public final FloatValue incrementMultiplier;

    public DragIncrementModal() {
        this(0.01F);
    }

    public DragIncrementModal(float incrementMultiplier) {
        super("drag_increment", "description");
        this.addChild("increment_multiplier", this.incrementMultiplier = new FloatValue("increment_multiplier", incrementMultiplier));
    }

    @Override
    public IPropertyModifierReleasableCommand factory() {
        return new IPropertyModifierReleasableCommand<>(this) {
            private float originalIncrement;

            @Override
            public Queue<IControllerCommand> modify(DragValueSliderProp props) {
                this.originalIncrement = props.increment.get();
                return new LinkedList<>(List.of(props.increment.controller().set(DragIncrementModal.this.incrementMultiplier.get())));
            }

            @Override
            public Queue<IControllerCommand> onReleaseEvent(DragValueSliderProp props) {
                return new LinkedList<>(List.of(props.increment.controller().set(this.originalIncrement)));
            }
        };
    }

    @Override
    public boolean confirmOnReleaseDefault() {
        return true;
    }
}
