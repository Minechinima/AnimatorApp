package com.minechinima.operator.ot;

import com.minechinima.utils.BitOperationUtils;

public abstract non-sealed class AppOperatorType<C> extends AbstractOperatorType<C, AbstractOperatorType<C, ?>> {
    /* flags for cleaner constructor head */
    public static final int ADJUSTLAST = 1;
    public static final int UNDO = 1 << 1;
    public static final int REPEATLAST = 1 << 2;

    private final boolean supportsAdjustLast;
    private final boolean supportsRepeatLast;

    public AppOperatorType(String title, String description, int flags) {
        super(title, description, BitOperationUtils.isBitFlagSet(UNDO, flags));
        this.supportsAdjustLast = BitOperationUtils.isBitFlagSet(ADJUSTLAST, flags);
        this.supportsRepeatLast = BitOperationUtils.isBitFlagSet(REPEATLAST, flags);
    }
}
