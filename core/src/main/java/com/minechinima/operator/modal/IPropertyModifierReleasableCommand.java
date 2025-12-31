package com.minechinima.operator.modal;

import com.minechinima.controller.IControllerCommand;
import com.minechinima.operator.OperatorProperties;
import com.minechinima.operator.config.modal.PropertyModifierReleaseableConfig;

import java.util.Queue;

public abstract class IPropertyModifierReleasableCommand<P extends OperatorProperties, C extends PropertyModifierReleaseableConfig<P>> extends IPropertyModifierCommand<P, C> {
    public IPropertyModifierReleasableCommand(C modifierConfig) {
        super(modifierConfig);
    }

    /**
     * @param props
     * @implNote Is executed on the same instance -> cached attributes can be used for persistent storage till this event is fired.
     */
    public abstract Queue<IControllerCommand> onReleaseEvent(P props);
}
