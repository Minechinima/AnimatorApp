package com.minechinima.operator.ot.commands;

import com.minechinima.operator.IContext;
import org.jetbrains.annotations.Nullable;

/**
 * For Operators that want to return a dynamic title based on their execution.
 * @param <C>
 */
public interface ITitleProvider<C extends IContext> extends IOperator<C> {
    /**
     * @param context
     * @return null if the title of the {@link com.minechinima.operator.ot.OperatorType} should be used.
     */
    @Nullable
    String getTitle(C context);
}
