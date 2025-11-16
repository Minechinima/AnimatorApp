package com.minechinima.operator.ot.commands.flags;

public enum OpInvokeFlag {
    /**
     * Operator exited prematurely and no changes should be made (revert)
     * and no undo step should be created.
     */
    CANCELLED,
    /**
     * Operator finished its job successfully.
     * This might have caused changes in the data.
     */
    FINISHED,
    /**
     * Spawn a UI and keep the operator alive,
     * notifying the operator of changes in the spawned UI.
     */
    RUN_IN_UI,
    /**
     * Execute the modal() method. If modal() returns this,
     * modal() will be executed again in the next update cycle.
     */
    MODAL_RUNNING;

    public static OpInvokeFlag from(OpExecuteFlag flag) {
        return switch (flag) {
            case FINISHED -> FINISHED;
            case CANCELLED -> CANCELLED;
            default -> throw new IllegalStateException("Unexpected value: " + flag);
        };
    }
}
