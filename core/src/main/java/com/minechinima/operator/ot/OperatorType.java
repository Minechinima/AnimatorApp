package com.minechinima.operator.ot;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.utils.BitOperationUtils;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @param <T> OperatorTypes that can be switched to while in modal mode. TODO check if this is necessary
 * @param <C> type of the Operator Context.
 * @implNote By defining {@link #poll(C)} here
 *           it allows generic bounds and type safety with the Context type of {@link IOperator <C>}.
 */
public abstract class OperatorType<C extends IContext> extends ValueGroup {
    /* flags for cleaner constructor head */
    public static final int ADJUSTLAST = 1;
    public static final int UNDO = 1 << 1;
    public static final int REPEATLAST = 1 << 2;

    public final String title;
    public final String description;

    /**
     * A set of other OperatorTypes that can replace this
     * while this is running in modal mode.
     */
    private final Set<OperatorType<C>> switchToTypes = new HashSet<>();
    /**
     * A Queue of OperatorTypes that will be invoked after this is finished.
     * This allows creating a chain and reusing more atomic Operators.
     */
    private final Queue<OperatorType<C>> macros = new LinkedList<>();
    /**
     * Whether this OperatorType, when successfully finished, should be pushed to history.
     */
    private final boolean pushUndo;
    private final boolean supportsAdjustLast;
    private final boolean supportsRepeatLast;

    public OperatorType(String id, String title, String description, int flags) {
        super(id);
        this.title = title;
        this.description = description;
        this.pushUndo = BitOperationUtils.isBitFlagSet(UNDO, flags);
        this.supportsAdjustLast = BitOperationUtils.isBitFlagSet(ADJUSTLAST, flags);
        this.supportsRepeatLast = BitOperationUtils.isBitFlagSet(REPEATLAST, flags);
    }

    public OperatorType(String id, String title, String description) {
        this(id, title, description, 0);
    }

    public boolean pushUndo() {
        return this.pushUndo;
    }

    public boolean supportsAdjustLast() {
        return this.supportsAdjustLast;
    }

    public boolean supportsRepeatLast() {
        return this.supportsRepeatLast;
    }

    /**
     * Whether the Operator can be executed in the given context.
     * @param context
     * @return false if the Operator cannot be executed.
     * @throws UserException with a meaningful message, if it should be displayed.
     * @implNote This is declared in the OperatorType to avoid unnecessary instantiation of an Operator or OperatorProperties.
     */
    public abstract boolean poll(C context) throws UserException;

}
