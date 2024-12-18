package com.minechinima.operator.defaults;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.Operator;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.flags.OpExecuteFlag;

public class RedoOperator<C extends OperatorContext> extends Operator<OperatorProperties, C> {
    public RedoOperator(OperatorProperties props) {
        super(props);
    }

    public OpExecuteFlag execute(OperatorContext context) throws UserException {
        context.uiManager.getOperatorManager().redo();
        return OpExecuteFlag.FINISHED;
    }
}
