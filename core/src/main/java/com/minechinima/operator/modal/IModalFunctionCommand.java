package com.minechinima.operator.modal;

import com.minechinima.operator.IContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.config.modal.ModalFunctionConfig;
import com.minechinima.operator.ot.commands.IModalOperator;

public abstract class IModalFunctionCommand<CTX extends IContext, CFG extends ModalFunctionConfig<CTX>> {
    protected final CFG config;

    public IModalFunctionCommand(CFG config) {
        this.config = config;
    }

    public abstract void onEvent(OperatorManager<CTX> operatorManager, CTX context, IModalOperator<CTX> currentModal);
}
