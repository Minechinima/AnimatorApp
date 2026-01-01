package com.minechinima.operator;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.operator.properties.TranslateProps;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.Queue;

public class TranslateObject extends IPropertyOperator<BBOperatorContext, TranslateProps> implements IRepeatableOperator<BBOperatorContext>, IModalOperator<BBOperatorContext> {
    private Vector3f startTranslation;

    public TranslateObject(TranslateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
        this.startTranslation = context.bbContext().activeObject.get().translation.asVector3f();
        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpExecuteFlag.CANCELLED);
        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue translation = object.translation;
        Vector3f offset = this.props.value.asVector3f();
        Vector3f rot = object.rotation.asVector3f();
        if (this.props.local.get()) {
            Matrix3f rotM = new Matrix3f();
            rotM.rotateX((float) Math.toRadians(rot.x));
            rotM.rotateY((float) Math.toRadians(rot.y));
            rotM.rotateZ((float) Math.toRadians(rot.z));
            rotM.transform(offset);
        }

        return new OPReturnValue<>(OpExecuteFlag.FINISHED, translation.controller().set(offset.add(this.startTranslation)));
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return this.execute(context);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> repeat(BBOperatorContext context) throws UserException {
        return this.execute(context);
    }

    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.005));
            this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.005));
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue translation = object.translation;
        Vector3f offset = this.props.value.asVector3f();
        Vector3f rot = object.rotation.asVector3f();
        if (this.props.local.get()) {
            Matrix3f rotM = new Matrix3f();
            rotM.rotateX((float) Math.toRadians(rot.x));
            rotM.rotateY((float) Math.toRadians(rot.y));
            rotM.rotateZ((float) Math.toRadians(rot.z));
            rotM.transform(offset);
        }

        if (this.props.transformOnAxis.get()) {
            switch (this.props.axis.get()) {
                case X -> {
                    offset.y = 0;
                    offset.z = 0;
                }
                case Y -> {
                    offset.x = 0;
                    offset.z = 0;
                }
                case Z -> {
                    offset.x = 0;
                    offset.y = 0;
                }
            }
        }


        IControllerCommand cmd = translation.controller().set(offset.add(this.startTranslation));
        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), cmd);
    }
}
