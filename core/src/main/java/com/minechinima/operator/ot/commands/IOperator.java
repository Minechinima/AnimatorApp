package com.minechinima.operator.ot.commands;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.utils.Pair;

import java.io.Closeable;
import java.util.Queue;

public interface IOperator<C extends IContext> extends Closeable {
    default Pair<OpExecuteFlag, Queue<IControllerCommand>> execute(C context) throws UserException {
        throw new UnsupportedOperationException("Method execute() not implemented.");
    }

    /**
     * Called when the operator is canceled by external system by outside conditions that are out of control of this.
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

    Pair<OpInvokeFlag, Queue<IControllerCommand>> invoke(C context) throws UserException;
}
