package com.minechinima.operator.modal;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.modal.PropertyModifierConfig;

import java.util.Queue;

/**
 * Command that modifies the properties of an Operator while it is running in modal mode to influence its execution logic.
 * <br><br>
 * This command is cleaned up after execution finished, it cannot be used to build a state machine.
 * @param <P>
 * @param <C> the config that spawned this command.
 */
public abstract class IPropertyModifierCommand<P extends OperatorProperties, C extends PropertyModifierConfig<P>> {
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
    public abstract IControllerCommand modify(P props);
}
