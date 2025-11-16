package com.minechinima.operator.config.modal;

import com.minechinima.model.values.base.ValueGroup;
import com.minechinima.operator.IContext;
import com.minechinima.operator.modal.IModalFunctionCommand;
import com.minechinima.ui.utils.ILangKeyProvider;

/**
 * very general purpose function that can be executed for any modal operator
 * and receives all important states (makes it more dependent and increases risks)
 * <br><br>
 * The command methods are executed on the configuration instances, YOU CANNOT STORE RUNTIME STATES HERE AS THESE INSTANCES ARE REUSED.
 * <br><br>
 * Is part of data model to allow configuration attributes
 */
public abstract class ModalFunctionConfig<C extends IContext> extends ValueGroup implements ILangKeyProvider {
    private final String title;
    private final String description;

    public ModalFunctionConfig(String title, String description) {
        super("");
        this.title = title;
        this.description = description;
    }

    @Override
    public String title() {
        return this.title;
    }

    public abstract IModalFunctionCommand<C, ?> factory();
}
