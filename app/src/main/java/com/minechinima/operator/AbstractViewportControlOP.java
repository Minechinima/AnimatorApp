package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.ui.panels.UIViewport;
import com.minechinima.utils.Pair;
import org.joml.Vector3d;

public abstract class AbstractViewportControlOP implements IModalOperator<BBOperatorContext> {
    protected UIViewport viewport;
    private float originalYaw;
    private float originalPitch;
    private float originalRoll;
    private Vector3d originalPos;
    private Vector3d originalOrbit;

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (!(context.getUIContext().clickedUISpace() instanceof UIViewport viewport)) {
            return new OPReturnValue<>(OpInvokeFlag.CANCELLED, null);
        }

        this.viewport = viewport;
        this.originalYaw = viewport.getYaw();
        this.originalPitch = viewport.getPitch();
        this.originalRoll = viewport.getRoll();
        this.originalPos = new Vector3d(viewport.getPos());
        this.originalOrbit = new Vector3d(viewport.getOrbitPos());

        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING, null);
    }

    @Override
    public void cancel() {
        this.viewport.setYaw(this.originalYaw);
        this.viewport.setPitch(this.originalPitch);
        this.viewport.setRoll(this.originalRoll);
        this.viewport.getPos().set(this.originalPos);
        this.viewport.getOrbitPos().set(this.originalOrbit);
    }
}
