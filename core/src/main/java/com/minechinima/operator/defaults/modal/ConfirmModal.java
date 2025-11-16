package com.minechinima.operator.defaults.modal;

import com.minechinima.operator.IContext;
import com.minechinima.operator.modal.IModalFunctionCommand;
import com.minechinima.operator.config.modal.ModalFunctionConfig;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.ot.commands.IModalOperator;

public class ConfirmModal<C extends IContext> extends ModalFunctionConfig<C> {
    public ConfirmModal() {
        super("confirm", "description");
    }

    @Override
    public IModalFunctionCommand<C, ?> factory() {
        return new IModalFunctionCommand<>(this) {
            @Override
            public void onEvent(OperatorManager<C> operatorManager, C context, IModalOperator<C> currentModal) {
                operatorManager.confirmModal();
            }
        };
    }
}
