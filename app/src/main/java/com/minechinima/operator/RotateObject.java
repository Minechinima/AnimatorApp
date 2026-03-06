package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.vectors.Vector3fValue;
import org.joml.Vector3d;

public class RotateObject extends AbstractTransformOperator<RotateProps> {

    public RotateObject(RotateProps props) {
        super(props);
    }

    @Override
    protected Vector3fValue getTargetValue(BaseObject object) {
        return object.rotation;
    }

    @Override
    protected void applyMouseInput(BBOperatorContext context, KeyMapMouseMoveEvent moveEvent) {
        if (this.props.trackball.get()) {
            this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragY() * 0.5));
            this.props.value.y.set((float) (this.props.value.y.get() + moveEvent.dragX() * 0.5));
            return;
        }

        if (this.viewport == null) return;

        Vector3d viewVector = this.viewport.getViewVector();
        float angle = (float) (moveEvent.dragX() * 0.5);
        this.props.value.x.set((float) (this.props.value.x.get() + angle * viewVector.x));
        this.props.value.y.set((float) (this.props.value.y.get() + angle * viewVector.y));
        this.props.value.z.set((float) (this.props.value.z.get() + angle * viewVector.z));
    }
}
