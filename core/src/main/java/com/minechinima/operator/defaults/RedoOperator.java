package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.utils.Pair;

public class RedoOperator implements IOperator<IContext> {
    @Override
    public OPReturnValue<OpInvokeFlag> invoke(IContext context) throws UserException {
        context.getUIContext().manager().getOperatorManager().redo();
        return new OPReturnValue<>(OpInvokeFlag.FINISHED, null);
    }
}
