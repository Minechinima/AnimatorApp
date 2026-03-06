package com.minechinima.operator.modal;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;

import java.util.Queue;

/**
 * Command that modifies the properties of an Operator while it is running in modal mode to influence its execution logic.
 * <br><br>
 * This command can react to release events, allowing to implement "holding key down" functionality.
 * <br><br>
 * The command is stored for the duration the key is held down, allowing to build a state machine and use cache variables.
 * @param <P>
 * @param <C> the config that spawned this command.
 */
public abstract class IPropertyModifierReleasableCommand<P extends OperatorProperties, C extends PropertyModifierReleaseableConfig<P>> extends IPropertyModifierCommand<P, C> {
    public IPropertyModifierReleasableCommand(C modifierConfig) {
        super(modifierConfig);
    }

    /**
     * Is only called when {@link PropertyModifierReleaseableConfig#useReleaseEvent()} is true.
     * @param props
     * @implNote Is executed on the same instance -> cached attributes can be used for persistent storage as a state machine utill this event is fired.
     */
    public abstract IControllerCommand onReleaseEvent(P props);
}
