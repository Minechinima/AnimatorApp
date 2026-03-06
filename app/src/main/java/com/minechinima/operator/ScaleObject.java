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
import com.minechinima.operator.properties.ScaleProps;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix3f;
import org.joml.Vector3f;

public class ScaleObject extends IPropertyOperator<BBOperatorContext, ScaleProps> implements IRepeatableOperator<BBOperatorContext>, IModalOperator<BBOperatorContext> {
    private Vector3f startScale;

    public ScaleObject(ScaleProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        this.startScale = object.scale.asVector3f();

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
        Vector3fValue scale = object.scale;
        Vector3f offset = computeOffset();

        return new OPReturnValue<>(OpExecuteFlag.FINISHED, scale.controller().set(offset.add(this.startScale)));
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> repeat(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        this.startScale = context.bbContext().activeObject.get().scale.asVector3f();
        return this.execute(context);
    }

    @NotNull
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.005));
            this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.005));
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue scale = object.scale;
        Vector3f offset = computeOffset();

        IControllerCommand cmd = scale.controller().set(offset.add(this.startScale));
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
