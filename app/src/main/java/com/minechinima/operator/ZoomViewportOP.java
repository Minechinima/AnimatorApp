package com.minechinima.operator;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.BBContext;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.operator.ot.commands.OpExecuteFlag;
import com.minechinima.operator.ot.commands.OpInvokeFlag;
import com.minechinima.operator.ot.commands.OpModalFlag;
import com.minechinima.ui.panels.UIViewport;
import com.minechinima.utils.Pair;
import org.joml.Matrix3d;
import org.joml.Vector3d;

import java.util.Queue;

public class ZoomViewportOP extends IPropertyOperator<BBOperatorContext, ZoomViewportProps> {
    public ZoomViewportOP(ZoomViewportProps props) {
        super(props);
    }

    @Override
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(BBOperatorContext context) throws UserException {
        if (!(context.getUIContext().clickedUISpace() instanceof UIViewport viewport)) {
            return new Pair<>(OpInvokeFlag.CANCELLED, null);
        }

        Vector3d viewVector = viewport.getViewVector();
        viewVector.mul(this.props.delta.get());
        viewport.getPos().add(viewVector);

        return new Pair<>(OpInvokeFlag.FINISHED, null);
    }

    @Override
    public Pair<OpExecuteFlag, Queue<IControllerCommand>> onUIPropChange(OperatorContext context, BaseStructure changedProp) throws UserException {
        return null;
    }
}
