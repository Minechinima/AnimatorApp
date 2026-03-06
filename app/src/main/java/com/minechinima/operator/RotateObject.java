package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.ui.utils.Area;
import org.joml.Matrix4d;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4d;

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

        BaseObject object = context.bbContext().activeObject.get();
        if (object == null) return;

        // Project the object's world position onto the screen
        Vector3f objectWorldPos = object.translation.asVector3f();
        Vector3d camPos = new Vector3d(this.viewport.getPos());

        Matrix4d viewMatrix = new Matrix4d()
                .rotateX(Math.toRadians(-this.viewport.getPitch()))
                .rotateY(Math.toRadians(-this.viewport.getYaw()))
                .translate(-camPos.x, -camPos.y, -camPos.z);

        Matrix4d vpMatrix = new Matrix4d(this.viewport.getProjection()).mul(viewMatrix);
        Vector4d clip = vpMatrix.transform(new Vector4d(objectWorldPos.x, objectWorldPos.y, objectWorldPos.z, 1.0));

        if (Math.abs(clip.w) < 1e-6) return;

        double ndcX = clip.x / clip.w;
        double ndcY = clip.y / clip.w;

        Area contentArea = this.viewport.getArea();
        double screenObjX = (ndcX + 1.0) / 2.0 * contentArea.getWidth() + contentArea.getX();
        double screenObjY = (1.0 - ndcY) / 2.0 * contentArea.getHeight() + contentArea.getY();

        // Vectors from projected object center to previous and current mouse positions
        // Negate Y so that the math convention (Y up) matches the screen (Y down)
        double prevDx = moveEvent.prevMouseX() - screenObjX;
        double prevDy = -(moveEvent.prevMouseY() - screenObjY);
        double currDx = moveEvent.mouseX() - screenObjX;
        double currDy = -(moveEvent.mouseY() - screenObjY);

        // Signed angle between the two vectors (atan2 of 2D cross product and dot product)
        double cross = prevDx * currDy - prevDy * currDx;
        double dot = prevDx * currDx + prevDy * currDy;
        float deltaAngle = (float) Math.toDegrees(Math.atan2(cross, dot));

        // Apply the angle as rotation around the viewport's view vector axis
        Vector3d viewVector = this.viewport.getViewVector();
        this.props.value.x.set((float) (this.props.value.x.get() + deltaAngle * viewVector.x));
        this.props.value.y.set((float) (this.props.value.y.get() + deltaAngle * viewVector.y));
        this.props.value.z.set((float) (this.props.value.z.get() + deltaAngle * viewVector.z));
    }
}
