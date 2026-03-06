package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.properties.TranslateProps;
import com.minechinima.ui.utils.Area;
import com.minechinima.utils.MathUtils;
import com.minechinima.utils.RenderingMathUtils;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class TranslateObject extends AbstractTransformOperator<TranslateProps> {

    public TranslateObject(TranslateProps props) {
        super(props);
    }

    @Override
    protected Vector3fValue getTargetValue(BaseObject object) {
        return object.translation;
    }

    @Override
    protected void applyMouseInput(BBOperatorContext context, KeyMapMouseMoveEvent moveEvent) {
        if (this.viewport == null) return;

        Matrix4d projection = this.viewport.getProjection();
        Matrix3d viewRotation = this.viewport.getViewRotation();
        Vector3d viewVector = this.viewport.getViewVector();
        Vector3d cameraPos = new Vector3d(this.viewport.getPos());
        Vector3d objectPos = new Vector3d(this.startValue);

        Area contentArea = this.viewport.getArea();
        double nx = MathUtils.mapRange(moveEvent.mouseX(), contentArea.getX(), contentArea.getEndX(), -1, 1);
        double ny = -MathUtils.mapRange(moveEvent.mouseY(), contentArea.getY(), contentArea.getEndY(), -1, 1);
        double nx0 = MathUtils.mapRange(moveEvent.prevMouseX(), contentArea.getX(), contentArea.getEndX(), -1, 1);
        double ny0 = -MathUtils.mapRange(moveEvent.prevMouseY(), contentArea.getY(), contentArea.getEndY(), -1, 1);

        RenderingMathUtils.projectScreenOntoPlane(cameraPos, projection, viewRotation, nx, ny, viewVector, objectPos)
                .ifPresent(destination -> {
                    RenderingMathUtils.projectScreenOntoPlane(cameraPos, projection, viewRotation, nx0, ny0, viewVector, objectPos)
                            .ifPresent(origin -> {
                                Vector3d delta = destination.sub(origin);
                                this.props.value.x.set((float) (this.props.value.x.get() + delta.x));
                                this.props.value.y.set((float) (this.props.value.y.get() + delta.y));
                                this.props.value.z.set((float) (this.props.value.z.get() + delta.z));
                            });
                });
    }
}
