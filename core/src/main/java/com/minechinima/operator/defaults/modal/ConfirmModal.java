package com.minechinima.operator.defaults.modal;

import com.minechinima.operator.IContext;
import com.minechinima.operator.ModalFunctionGroupStruct;
import com.minechinima.operator.OperatorContext;
import com.minechinima.operator.OperatorManager;
import com.minechinima.operator.ot.commands.IModalOperator;

public class ConfirmModal<C extends IContext> extends ModalFunctionGroupStruct<C> {
    public ConfirmModal() {
        super("confirm", "description");
    }

    @Override
    public void onEvent(OperatorManager<C> operatorManager, OperatorContext<C> context, IModalOperator<C> currentModal) {
        operatorManager.confirmModal();
    }
}
