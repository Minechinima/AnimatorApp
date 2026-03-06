package com.minechinima.operator.modal;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.config.modal.ModalFunctionReleasableConfig;
import com.minechinima.operator.config.modal.PropertyModifierReleasableCommandConfig;
import com.minechinima.operator.ot.commands.IModalOperator;

/**
 * A command that can be executed for any operator during modal mode. This can be used to modify the system.
 * This is not used to modify the properties and execution of one specific Operator, see {@link IPropertyModifierReleasableCommand}.
 * <br><br>
 * This command can react to release events, allowing to implement "holding key down" functionality.
 * <br><br>
 * The command is stored for the duration the key is held down, allowing to build a state machine and use cache variables.
 * @param <CTX>
 * @param <CFG>
 */
public abstract class IModalFunctionReleasableCommand<CTX extends IContext, CFG extends ModalFunctionReleasableConfig<CTX>>
        extends IModalFunctionCommand<CTX, CFG> {

    public IModalFunctionReleasableCommand(CFG config) {
        super(config);
    }

    /**
     * Is only called when {@link PropertyModifierReleasableCommandConfig#useReleaseEvent()} is true.
     * @param operatorManager
     * @param context
     * @param currentModal
     */
    public abstract void onReleaseEvent(OperatorManager<CTX> operatorManager,
                                        CTX context,
                                        IModalOperator<CTX> currentModal);
}
