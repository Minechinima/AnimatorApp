package com.minechinima.operator.ot.commands;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import org.jetbrains.annotations.NotNull;

import java.io.Closeable;

/**
 * The execution logic of one user action.
 * The command instance is stored during its lifetime, it can be used as a state machine.
 * @param <C>
 */
public interface IOperator<C extends IContext> extends Closeable {
    @NotNull
    default OPReturnValue<OpExecuteFlag> execute(C context) throws UserException {
        throw new UnsupportedOperationException("Method execute() not implemented.");
    }

    /**
     * Called when the operator is canceled by external reasons for example unexpected Exceptions.
     */
    default void cancel() {

    }

    /**
     * Close method called for all cases where the operator is closed / stopped being handled by the OperatorManager.
     * This includes after success, finish flag, errors, cancel() being called, invoke/execute method finished.<br><br>
     * This can be used for cleanup.
     */
    @Override
    default void close() {

    }

    @NotNull
    OPReturnValue<OpInvokeFlag> invoke(C context) throws UserException;
}
