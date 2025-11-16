package com.minechinima.operator.ot.commands;

import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.ui.elements.UIElement;
import org.jetbrains.annotations.Nullable;

/**
 * For Operators that support adjust last operation from the history.
 * //TODO idea how adjust last works:
 * The actions performed by the operator before "adjust last" are not undone or modified in any way, this operator
 * is applied on top of them.
 * When the "adjust last" is successful the history steps are merged.
 * @param <C>
 * @param <P>
 */
public abstract class IAdjustableOperator<C extends IContext, P extends OperatorPropertiesGroupStruct> extends IPropertyOperator<C, P> {
    public IAdjustableOperator(P props) {
        super(props);
    }

    /**
     * Called at the start of an adjustment operation.
     * @param context
     * @return
     * @throws UserException
     */
    public OPReturnValue<OpExecuteFlag> invokeAdjustment(C context) throws UserException {
        return new OPReturnValue<>(OpExecuteFlag.FINISHED);
    }

    /**
     * Called when a property changes from the Operator AdjustLast UI Popup.
     * @param context
     * @param prop the property that was changed
     */
    public abstract OPReturnValue<OpExecuteFlag> adjust(C context, BaseStructure prop) throws UserException;

    /**
     * Called when the adjust last action is confirmed and the UI is closed.
     * <br><br>
     * This can be used to only perform the adjust last operation on confirmation instead of every property change.
     * @param context
     * @return
     */
    public OPReturnValue<OpExecuteFlag> confirmAdjustment(C context) throws UserException {
        return new OPReturnValue<>(OpExecuteFlag.FINISHED);
    }

    /**
     * @return if null the auto generated UI is taken.
     */
    @Nullable
    public UIElement adjustLastUI() {
        return null;
    }
}
