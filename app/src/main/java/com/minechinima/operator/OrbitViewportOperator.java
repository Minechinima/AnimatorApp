package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.utils.Pair;
import org.joml.Vector3d;

import java.util.Queue;

public class OrbitViewportOperator extends AbstractViewportControlOP {
    @Override
    public Pair<Modal, Queue<IControllerCommand>> modal(OperatorContext<BBOperatorContext> context, KeyMapEvent event) throws UserException {
        Vector3d relativePosition = new Vector3d(this.viewport.getPos()).sub(this.viewport.getOrbitPos());

        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            this.viewport.setYaw((float) (this.viewport.getYaw() + moveEvent.dragX() * 0.25F));
            this.viewport.setPitch((float) (this.viewport.getPitch() - moveEvent.dragY() * 0.25F));

            Vector3d basis = new Vector3d(0, 0, relativePosition.length());
            this.viewport.getViewRotation().transform(basis);

            this.viewport.getPos().set(basis.add(this.viewport.getOrbitPos()));
        }

        return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), null);
    }
}
