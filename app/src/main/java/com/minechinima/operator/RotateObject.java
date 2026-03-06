package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.OrientationEnumValueStruct;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class RotateObject extends IPropertyOperator<BBOperatorContext, RotateProps> implements IRepeatableOperator<BBOperatorContext>, IModalOperator<BBOperatorContext> {
    private Vector3f startRotation;

    public RotateObject(RotateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        this.startRotation = object.rotation.asVector3f();

        if (this.props.orientation.get() == OrientationEnumValueStruct.Orientation.LOCAL) {
            Vector3f rot = object.rotation.asVector3f();
            Matrix3f rotM = new Matrix3f();
            rotM.rotateX((float) Math.toRadians(rot.x));
            rotM.rotateY((float) Math.toRadians(rot.y));
            rotM.rotateZ((float) Math.toRadians(rot.z));
            this.props.orientationMatrix.set(rotM);
        } else {
            this.props.orientationMatrix.set(new Matrix3f());
        }

        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue rotation = object.rotation;
        Vector3f offset = computeOffset();

        return new OPReturnValue<>(OpExecuteFlag.FINISHED, rotation.controller().set(offset.add(this.startRotation)));
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> repeat(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        this.startRotation = context.bbContext().activeObject.get().rotation.asVector3f();
        return this.execute(context);
    }

    @NotNull
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            if (this.props.trackball.get()) {
                this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragY() * 0.5));
                this.props.value.y.set((float) (this.props.value.y.get() + moveEvent.dragX() * 0.5));
            } else {
                this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.5));
                this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.5));
            }
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue rotation = object.rotation;
        Vector3f offset = computeOffset();

        IControllerCommand cmd = rotation.controller().set(offset.add(this.startRotation));
        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), cmd);
    }

    private Vector3f computeOffset() {
        Vector3f offset = this.props.value.asVector3f();
        offset.mul(this.props.scaleFactor.get());

        Matrix3f orientationMatrix = this.props.orientationMatrix.asMatrix3f();
        orientationMatrix.transform(offset);

        boolean cx = this.props.constraintX.get();
        boolean cy = this.props.constraintY.get();
        boolean cz = this.props.constraintZ.get();
        if (cx || cy || cz) {
            if (!cx) offset.x = 0;
            if (!cy) offset.y = 0;
            if (!cz) offset.z = 0;
        }

        return offset;
    }
}
