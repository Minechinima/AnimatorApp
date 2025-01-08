package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.OpExecuteFlag;
import com.minechinima.operator.ot.commands.OpInvokeFlag;
import com.minechinima.ui.BBOperatorContext;
import com.minechinima.utils.Pair;

import java.util.Queue;

public class DeleteObject implements IOperator<BBOperatorContext> {
    public Pair<OpExecuteFlag, Queue<Object>> execute(BBOperatorContext context) throws UserException {
        return new Pair<>(OpExecuteFlag.FINISHED, null);
    }

    @Override
    public Pair<OpInvokeFlag, Queue<Object>> invoke(BBOperatorContext context) throws UserException {
        Pair<OpExecuteFlag, Queue<Object>> retval = this.execute(context);
        return new Pair<>(OpInvokeFlag.from(retval.getA()), retval.getB());
    }
}
