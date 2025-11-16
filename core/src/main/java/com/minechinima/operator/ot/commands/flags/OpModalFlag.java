package com.minechinima.operator.ot.commands.flags;

public enum OpModalFlag {
    CANCELLED,
    FINISHED,
    MODAL_RUNNING;

    public static OpModalFlag from(OpExecuteFlag flag) {
        return switch (flag) {
            case FINISHED -> FINISHED;
            case CANCELLED -> CANCELLED;
            default -> throw new IllegalStateException("Unexpected value: " + flag);
        };
    }
}
