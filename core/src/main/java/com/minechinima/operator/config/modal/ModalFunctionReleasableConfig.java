package com.minechinima.operator.config.modal;

import com.minechinima.model.values.BooleanValue;
import com.minechinima.operator.IContext;
import com.minechinima.operator.modal.IModalFunctionReleasableCommand;

public abstract class ModalFunctionReleasableConfig<C extends IContext> extends ModalFunctionConfig<C> {
    public final BooleanValue toggleOnRelease = new BooleanValue("toggle_on_release", false);

    public ModalFunctionReleasableConfig(String title, String description) {
        super(title, description);
        this.addChild("toggle_on_release", this.toggleOnRelease);
    }

    @Override
    public abstract IModalFunctionReleasableCommand<C, ?> factory();
}
