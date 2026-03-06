package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.properties.ScaleProps;

public class ScaleObject extends AbstractTransformOperator<ScaleProps> {

    public ScaleObject(ScaleProps props) {
        super(props);
    }

    @Override
    protected Vector3fValue getTargetValue(BaseObject object) {
        return object.scale;
    }

    @Override
    protected void applyMouseInput(BBOperatorContext context, KeyMapMouseMoveEvent moveEvent) {
        this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.005));
        this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.005));
    }
}
