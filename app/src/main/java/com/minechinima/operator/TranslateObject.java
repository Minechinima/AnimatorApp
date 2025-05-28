package com.minechinima.operator;

import com.minechinima.controller.ControllerUtils;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.input.keymap.events.KeyMapMouseMoveEvent;
import com.minechinima.model.AxisEnumValueStruct;
import com.minechinima.model.BBContext;
import com.minechinima.model.app.object.BaseObject;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.vectors.Vector3fValue;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.utils.Pair;
import org.joml.Matrix3f;
import org.joml.Vector3f;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class TranslateObject extends IPropertyOperator<BBOperatorContext, TranslateProps> implements IRepeatableOperator<BBOperatorContext>, IModalOperator<BBOperatorContext> {
    private Vector3f startTranslation;

    public TranslateObject(TranslateProps props) {
        super(props);
    }

    @Override
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.getReference() == null) return new Pair<>(OpInvokeFlag.CANCELLED, null);
        this.startTranslation = context.bbContext().activeObject.getReference().translation.asVector3f();
        return new Pair<>(OpInvokeFlag.MODAL_RUNNING, null);
    }

    @Override
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> execute(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.getReference() == null) return new Pair<>(OpExecuteFlag.CANCELLED, null);
        BaseObject object = context.bbContext().activeObject.getReference();
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

        Queue<IControllerCommand> cmds = ControllerUtils.asQueue(translation.controllers().set(offset.add(this.startTranslation)));
        return new Pair<>(OpExecuteFlag.FINISHED, cmds);
    }

    @Override
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> onUIPropChange(OperatorContext<BBOperatorContext> context, BaseStructure changedProp) throws UserException {
        return this.execute(context.current());
    }

    @Override
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> repeat(BBOperatorContext context) throws UserException {
        return this.execute(context);
    }

    @Override
    public Pair<Modal, Queue<IControllerCommand>> modal(OperatorContext<BBOperatorContext> context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.005));
            this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.005));
        }

        BaseObject object = context.triggered().bbContext().activeObject.getReference();
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

        Queue<IControllerCommand> cmds = ControllerUtils.asQueue(translation.controllers().set(offset.add(this.startTranslation)));
        return new Pair<>(new Modal(OpModalFlag.MODAL_RUNNING), cmds);
    }
}
