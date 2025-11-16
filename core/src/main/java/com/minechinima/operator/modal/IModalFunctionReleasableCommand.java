package com.minechinima.operator.modal;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.config.modal.ModalFunctionReleasableConfig;
import com.minechinima.operator.ot.commands.IModalOperator;

public abstract class IModalFunctionReleasableCommand<CTX extends IContext, CFG extends ModalFunctionReleasableConfig<CTX>>
        extends IModalFunctionCommand<CTX, CFG> {

    public IModalFunctionReleasableCommand(CFG config) {
        super(config);
    }

    public abstract void onReleaseEvent(OperatorManager<CTX> operatorManager,
                                        CTX context,
                                        IModalOperator<CTX> currentModal);
}
