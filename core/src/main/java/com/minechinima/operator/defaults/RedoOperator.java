package com.minechinima.operator.defaults;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.OpInvokeFlag;
import com.minechinima.utils.Pair;

import java.util.Queue;

public class RedoOperator implements IOperator {
    @Override
    public Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(IContext context) throws UserException {
        context.getUIContext().manager().getOperatorManager().redo();
        return new Pair<>(OpInvokeFlag.FINISHED, null);
    }
}
