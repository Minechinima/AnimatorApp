package com.minechinima.operator;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.ui.panels.UIViewport;
import org.joml.Vector3d;

public class ZoomViewportOP extends IPropertyOperator<BBOperatorContext, ZoomViewportProps> {
    public ZoomViewportOP(ZoomViewportProps props) {
        super(props);
    }

    @Override
    public OPReturnValue<OpInvokeFlag> invoke(BBOperatorContext context) throws UserException {
        if (!(context.getUIContext().clickedUISpace() instanceof UIViewport viewport)) {
            return new OPReturnValue<>(OpInvokeFlag.CANCELLED, null);
        }

        Vector3d viewVector = viewport.getViewVector();
        viewVector.mul(this.props.delta.get());
        viewport.getPos().add(viewVector);

        return new OPReturnValue<>(OpInvokeFlag.FINISHED, null);
    }

    @Override
    public OPReturnValue<OpExecuteFlag> onUIPropChange(BBOperatorContext context, BaseStructure changedProp) throws UserException {
        return null;
    }
}
