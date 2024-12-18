package com.minechinima.operator;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.flags.OpExecuteFlag;

/**
 * This Operator Type can change App (data) and UI states.
 */
public abstract class Operator<T extends OperatorProperties, C extends OperatorContext> extends AbstractOperator<T, C> {
    /* runtime attributes */
    private boolean isReadjusted;
    private boolean isRepeated;

    public Operator(T props) {
        super(props);
    }

    public OpExecuteFlag execute(C context) throws UserException {
        throw new UnsupportedOperationException("Method execute() not implemented.");
    }
}
