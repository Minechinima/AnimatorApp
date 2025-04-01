package com.minechinima.operator.ot.commands;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.utils.Pair;

import java.util.Queue;

/**
 * For Operators that can be repeated from the history.
 * @param <C>
 */
public interface IRepeatableOperator<C extends IContext> extends IOperator<C> {
    /**
     * Called when the operator is repeated from the history
     * @param context
     */
    Pair<OpExecuteFlag, Queue<IControllerCommand>> repeat(C context) throws UserException;
}
