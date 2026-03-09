package com.minechinima.operator.ot.commands;

import com.minechinima.operator.IContext;

/**
 * An interface for operators that provide own undo capabilities, e.g. when
 * the operator changes something in the UI that is not recorded in the database.
 * <br><br>
 * @implNote this undo function is called after the database changes have been undone.
 */
public interface IUndoOperator<C extends IContext> extends IOperator<C> {
    IUndoOperatorCommand<C> getUndoCommand(C context);

    /**
     * Separate command interface. Implement this in a separate class or <b>static</b> inner class. It is important
     * that the original Operator instance is not referenced so it does not stay alive in the history stack.
     * @param <C>
     */
    interface IUndoOperatorCommand<C extends IContext> {
        void undo(C context);
        void redo(C context);
    }
}
