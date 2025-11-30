package com.minechinima.operator.modal;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.OperatorPropertiesGroupStruct;
import com.minechinima.operator.config.modal.PropertyModifierConfig;

import java.util.Queue;

public abstract class IPropertyModifierCommand<P extends OperatorPropertiesGroupStruct, C extends PropertyModifierConfig<P>> {
    protected final C modifierConfig;

    public IPropertyModifierCommand(C modifierConfig) {
        this.modifierConfig = modifierConfig;
    }

    /**
     * Modify the properties.
     * @param props
     * @implNote Does not return {@link IControllerCommand} so it cant perform complex logic and only
     * modify the properties.
     */
    public abstract Queue<IControllerCommand> modify(P props);
}
