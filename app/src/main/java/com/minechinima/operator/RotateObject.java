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
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class RotateObject extends IAdjustableOperator<BBOperatorContext, RotateProps> implements IModalOperator<BBOperatorContext> {
    private Vector3f startRotation;

    public RotateObject(RotateProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> adjust(BBOperatorContext context, BaseStructure prop) throws UserException {
        return null;
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return null;
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (context.bbContext().activeObject.get() == null) return new OPReturnValue<>(OpInvokeFlag.CANCELLED);
        this.startRotation = context.bbContext().activeObject.get().translation.asVector3f();
        return new OPReturnValue<>(OpInvokeFlag.MODAL_RUNNING);
    }

    @NotNull
    @Override
    public OPReturnValue<Modal> modal(BBOperatorContext context, KeyMapEvent event) throws UserException {
        if (event instanceof KeyMapMouseMoveEvent moveEvent) {
            this.props.value.x.set((float) (this.props.value.x.get() + moveEvent.dragX() * 0.5));
            this.props.value.y.set((float) (this.props.value.y.get() - moveEvent.dragY() * 0.5));
        }

        BaseObject object = context.bbContext().activeObject.get();
        Vector3fValue rotation = object.rotation;
        Vector3f offset = this.props.value.asVector3f();

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

        IControllerCommand cmd = rotation.controller().set(offset.add(this.startRotation));
        return new OPReturnValue<>(new Modal(OpModalFlag.MODAL_RUNNING), cmd);
    }
}
