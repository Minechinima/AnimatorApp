package com.minechinima.operator.ot.commands.flags;

/**
 * Return values for the operator methods that should end the operator.
 */
public enum OpExecuteFlag {
    /**
     * Operator exited prematurely and no changes should be made (revert)
     * and no undo step should be created.
     */
    CANCELLED,
    FINISHED;
}
