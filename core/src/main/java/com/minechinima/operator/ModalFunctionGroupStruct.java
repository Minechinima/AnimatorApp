package com.minechinima.operator;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.ot.commands.IModalOperator;
import com.minechinima.ui.utils.ILangKeyProvider;

/**
 * very general purpose function that can be executed for any modal operator
 * and receives all important states (makes it more dependent and increases risks)
 * <br><br>
 * The command methods are executed on the configuration instances, YOU CANNOT STORE RUNTIME STATES HERE AS THESE INSTANCES ARE REUSED.
 * <br><br>
 * Is part of data model to allow configuration attributes
 */
public abstract class ModalFunctionGroupStruct<C extends IContext> extends ValueGroup implements ILangKeyProvider {
    private final String title;
    private final String description;

    public ModalFunctionGroupStruct(String title, String description) {
        super("");
        this.title = title;
        this.description = description;
    }

    @Override
    public String title() {
        return this.title;
    }

    public abstract void onEvent(OperatorManager<C> operatorManager, OperatorContext<C> context, IModalOperator<C> currentModal);
}
