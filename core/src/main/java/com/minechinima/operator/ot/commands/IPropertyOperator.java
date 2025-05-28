package com.minechinima.operator.ot.commands;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.ui.elements.UIModal;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Queue;

/**
 *
 * @param <P>
 * @param <C> context
 * @implNote Important why this is an interface: force subclasses that
 * implement {@link IAdjustableOperator} to also implement this interface.
 */
public abstract class IPropertyOperator<C extends IContext, P extends OperatorPropertiesGroupStruct> implements IOperator<C> {
    @NotNull
    protected final P props;

    public IPropertyOperator(P props) {
        this.props = Objects.requireNonNull(props);
    }

    @NotNull
    public final P props() {
        return this.props;
    }

    /**
     * Called when Operator is running in UI Mode and the UI changes a property.
     * @param context
     * @param changedProp
     * @return
     * @throws UserException
     */
    public abstract Pair<OpExecuteFlag, Queue<IControllerCommand>> onUIPropChange(OperatorContext<C> context, BaseStructure changedProp) throws UserException;

    /**
     * @return if null the auto generated UI will be taken.
     */
    @Nullable
    public UIModal propUI() {
        return null;
    }
}
