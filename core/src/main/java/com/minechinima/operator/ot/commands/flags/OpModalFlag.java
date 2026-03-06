package com.minechinima.operator.ot.commands.flags;

public enum OpModalFlag {
    /**
     * Operator exited prematurely and no changes should be made (revert)
     * and no undo step should be created.
     */
    CANCELLED,
    /**
     * Operator is finished.
     */
    FINISHED,
    /**
     * Keep operator running for the next events.
     */
    MODAL_RUNNING;

    public static OpModalFlag from(OpExecuteFlag flag) {
        return switch (flag) {
            case FINISHED -> FINISHED;
            case CANCELLED -> CANCELLED;
            default -> throw new IllegalStateException("Unexpected value: " + flag);
        };
    }
}
