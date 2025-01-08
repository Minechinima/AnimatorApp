package com.minechinima.operator.ot.commands;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.ui.elements.UIElement;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.Queue;

/**
 * For Operators that support adjust last operation from the history.
 * @param <C>
 * @param <P>
 */
public interface IAdjustableOperator<C, P extends OperatorProperties> extends IPropertyOperator<C, P> {
    /**
     * Called when a property changes from the Operator AdjustLast UI Popup.
     * @param context
     * @param prop the property that was changed
     */
    Pair<OpExecuteFlag, Queue<Object>> adjust(C context, BaseStructure prop) throws UserException;

    /**
     * Called when the adjust last action is confirmed and the UI is closed.
     * <br><br>
     * This can be used to only perform the adjust last operation on confirmation instead of every property change.
     * @param context
     * @return
     */
    Pair<OpExecuteFlag, Queue<Object>> confirmAdjustment(C context) throws UserException;

    /**
     * Called when the adjust last action is canceled and the UI is closed.
     * <br><br>
     * This can be used to undo operations that are not part of the database.
     * @param context
     */
    void cancelAdjustment(C context) throws UserException;

    /**
     * @return if null the auto generated UI is taken.
     */
    @Nullable
    default UIElement adjustLastUI() {
        return null;
    }
}
