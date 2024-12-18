package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.Operator;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.flags.OpExecuteFlag;

public class UndoOperator<C extends OperatorContext> extends Operator<OperatorProperties, C> {
    public UndoOperator(OperatorProperties props) {
        super(props);
    }

    public OpExecuteFlag execute(C context) throws UserException {
        context.uiManager.getOperatorManager().undo();
        return OpExecuteFlag.FINISHED;
    }
}
