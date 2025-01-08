package com.minechinima.operator.ot.commands;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.CursorInfo;
import com.minechinima.utils.Pair;

import javax.annotation.Nullable;
import java.util.Queue;

public interface IOperator<C> {
    default Pair<OpExecuteFlag, Queue<Object>> execute(C context) throws UserException {
        throw new UnsupportedOperationException("Method execute() not implemented.");
    }

    Pair<OpInvokeFlag, Queue<Object>> invoke(C context) throws UserException;
}
