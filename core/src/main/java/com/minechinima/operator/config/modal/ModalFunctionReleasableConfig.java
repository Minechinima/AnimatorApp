package com.minechinima.operator.config.modal;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.IContext;
import com.minechinima.operator.config.IReleasableModule;
import com.minechinima.operator.modal.IModalFunctionReleasableCommand;

public abstract class ModalFunctionReleasableConfig<C extends IContext> extends ModalFunctionConfig<C> implements IReleasableModule {
    public ModalFunctionReleasableConfig(String title, String description) {
        super(title, description);
    }

    @Override
    public abstract IModalFunctionReleasableCommand<C, ?> factory();
}
