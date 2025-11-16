package com.minechinima.operator.ot.commands;

/**
 * An interface for operators that provide own undo capabilities, e.g. when
 * the operator changes something in the UI that is not recorded in the database.
 * <br><br>
 * @implNote this undo function is called after the database changes have been undone.
 */
public interface IUndoOperator {
    //TODO review if this needs context object and if so if it needs triggered and current context.
    void undo();
    void redo();
}
