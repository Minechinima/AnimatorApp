package com.minechinima.operator;

import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.ui.utils.Area;
import com.minechinima.utils.MathUtils;
import com.minechinima.utils.Pair;
import com.minechinima.utils.RenderingMathUtils;
import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Vector3d;

public class PanViewportOP extends AbstractViewportControlOP {
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event.getClass() != KeyMapMouseMoveEvent.class) return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), null);
        //TODO triggered multiple times because moseMoveEvent has many sublcasses...
        // what to do with subclasses, how do we prevent a modal from executing its logic multiple times
        Vector3d viewVector = new Vector3d(0,0,-1);
        KeyMapMouseMoveEvent mouseMoveEvent = (KeyMapMouseMoveEvent) event;

        Matrix4d projection = this.viewport.getProjection();
        Matrix3d viewRotation = this.viewport.getViewRotation();

        viewRotation.transform(viewVector);
        Vector3d cameraPos = new Vector3d(this.viewport.getPos());

        /*
         * When the camera is very near at the orbit position, panning is super slow.
         * Move the camera back (virtually) along the view vector to counter this a little bit.
         */
        double dist = new Vector3d(cameraPos).sub(this.viewport.getOrbitPos()).length();
        if (dist <= 0.05) {
            cameraPos.add(new Vector3d(viewVector).mul(-(0.05 - dist)));
        }

        Area contentArea = this.viewport.getArea();
        double nx = MathUtils.mapRange(event.mouseX(), contentArea.getX(), contentArea.getEndX(), -1, 1);
        double ny = -MathUtils.mapRange(event.mouseY(), contentArea.getY(), contentArea.getEndY(), -1, 1);
        double nx0 = MathUtils.mapRange(mouseMoveEvent.prevMouseX(), contentArea.getX(), contentArea.getEndX(), -1, 1);
        double ny0 = -MathUtils.mapRange(mouseMoveEvent.prevMouseY(), contentArea.getY(), contentArea.getEndY(), -1, 1);

        /* test projecting the origin and destination points to calculate how much to move in world space */
        Vector3d drag = new Vector3d();
        RenderingMathUtils.projectScreenOntoPlane(cameraPos, projection, viewRotation, nx, ny, viewVector, this.viewport.getOrbitPos())
                .ifPresent((destination) -> {
                    RenderingMathUtils.projectScreenOntoPlane(cameraPos, projection, viewRotation, nx0, ny0, viewVector, this.viewport.getOrbitPos())
                            .ifPresent((origin) -> drag.set(origin.sub(destination)));
                });

        this.viewport.getPos().add(drag);
        this.viewport.getOrbitPos().add(drag);

        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), null);
    }
}
