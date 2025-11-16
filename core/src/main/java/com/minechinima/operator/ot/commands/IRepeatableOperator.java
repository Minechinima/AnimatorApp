package com.minechinima.operator.ot.commands;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;

/**
 * For Operators that can be repeated from the history.
 * @param <C>
 */
public interface IRepeatableOperator<C extends IContext> extends IOperator<C> {
    /**
     * Called when the operator is repeated from the history
     * @param context
     */
    OPReturnValue<OpExecuteFlag> repeat(C context) throws UserException;
}
