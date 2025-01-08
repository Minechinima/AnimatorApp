package com.minechinima.operator.ot;

import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.ot.commands.IOperator;

import java.util.HashSet;
import java.util.Set;

/**
 * @param <T> OperatorTypes that can be switched to while in modal mode.
 * @param <C> type of the Operator Context.
 * @implNote By defining {@link #poll(C)} here
 *           it allows generic bounds and type safety with the Context type of {@link IOperator <C>}.
 */
public sealed abstract class AbstractOperatorType<C, T extends AbstractOperatorType<C, ?>> permits AppOperatorType, UIOperatorType {
    public final String title;
    public final String description;

    /**
     * A set of other operatorTypes that can replace this
     * while this is running in modal mode.
     */
    private final Set<T> switchToTypes = new HashSet<>();
    /**
     * Whether this OperatorType, when successfully finished, should be pushed to history.
     */
    private final boolean pushUndo;

    public AbstractOperatorType(String title, String description) {
        this(title, description, true);
    }

    public AbstractOperatorType(String title, String description, boolean pushUndo) {
        this.title = title;
        this.description = description;
        this.pushUndo = pushUndo;
    }

    public boolean pushUndo() {
        return this.pushUndo;
    }

    /**
     * Whether the Operator can be executed in the given context.
     * @param context
     * @return false if the Operator cannot be executed.
     * @throws UserException with a meaningful message, if it should be displayed.
     * @implNote This is declared in the OperatorType to avoid unnecessary instantiation of an Operator or OperatorProperties.
     */
    public abstract boolean poll(C context) throws UserException;

    /**
     * An OperatorType that provides OperatorProperties
     * @param <P> type of the operator properties
     */
    public interface PropertyProvider<P extends OperatorProperties> {
        P createProps();
    }
}
