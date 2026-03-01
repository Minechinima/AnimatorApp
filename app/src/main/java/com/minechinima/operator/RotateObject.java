package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RotateObject extends IAdjustableOperator<BBOperatorContext, RotateProps> implements IModalOperator<BBOperatorContext> {

    private static final float BASE_SENSITIVITY = 0.5f;

    private Vector3f startRotation;

    public RotateObject(RotateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
        this.startRotation = context.bbContext().activeObject.get().rotation.asVector3f();
        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        Vector3f offset = computeOffset();
        return new OPReturnValue<>(OpExecuteFlag.FINISHED, object.rotation.controller().set(offset.add(this.startRotation)));
    }

    @NotNull
    @Override
    public OPReturnValue<OpExecuteFlag> adjust(BBOperatorContext context, BaseStructure prop) throws UserException {
        return this.execute(context);
    }

    @NotNull
    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @NotNull
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            float sensitivity = BASE_SENSITIVITY * this.props.precision.get();
            switch (this.props.axisConstraint.get()) {
                case X -> this.props.value.x.set(this.props.value.x.get() + (float) (moveEvent.dragX() * sensitivity));
                case Y -> this.props.value.y.set(this.props.value.y.get() + (float) (moveEvent.dragX() * sensitivity));
                case Z -> this.props.value.z.set(this.props.value.z.get() + (float) (moveEvent.dragX() * sensitivity));
                default -> {
                    this.props.value.x.set(this.props.value.x.get() + (float) (moveEvent.dragX() * sensitivity));
                    this.props.value.y.set(this.props.value.y.get() - (float) (moveEvent.dragY() * sensitivity));
                }
            }
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3f offset = computeOffset();
        IControllerCommand cmd = object.rotation.controller().set(offset.add(this.startRotation));
        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), cmd);
    }

    private Vector3f computeOffset() {
        Vector3f offset = this.props.value.asVector3f();

        applyAxisConstraint(offset);

        if (this.props.snap.get()) {
            offset.x = Math.round(offset.x);
            offset.y = Math.round(offset.y);
            offset.z = Math.round(offset.z);
        }

        return offset;
    }

    private void applyAxisConstraint(Vector3f offset) {
        switch (this.props.axisConstraint.get()) {
            case X -> { offset.y = 0; offset.z = 0; }
            case Y -> { offset.x = 0; offset.z = 0; }
            case Z -> { offset.x = 0; offset.y = 0; }
            case XY -> offset.z = 0;
            case XZ -> offset.y = 0;
            case YZ -> offset.x = 0;
            case ALL -> {}
        }
    }
}
