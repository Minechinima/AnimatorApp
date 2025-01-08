package com.minechinima.operator.ot.commands;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 *
 * @param <P>
 * @param <C> context
 * @implNote Important why this is an interface: force subclasses that
 * implement {@link IAdjustableOperator} to also implement this interface.
 */
public interface IPropertyOperator<C, P extends OperatorProperties> extends IOperator<C> {
    @NotNull
    P props();

    /**
     * Called when Operator is running in UI Mode and the UI changes a property.
     * @param context
     * @param changedProp
     * @return
     * @throws UserException
     */
    Pair<OpExecuteFlag, Queue<Object>> executeOnPropChange(C context, BaseStructure changedProp) throws UserException;

    /**
     * Called when the Operator was running in UI Mode but the UI and Operator are canceled.
     * <br><br>
     * This can be used to undo operations that are not part of the database.
     * @param context
     */
    void cancelUIMode(C context);

    /**
     * @return if null the auto generated UI will be taken.
     */
    @Nullable
    default UIElement propUI() {
        return null;
    }
}
