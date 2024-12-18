package com.minechinima.operator;

import com.minechinima.controller.Controller;
import com.minechinima.ui.WindowManager;

/**
 * Manages running operators, handles triggering them and state changes.
 * <br><br>
 * This is the only place an Operator can be instantiated and triggered correctly.
 */
public class OperatorManager {
    //private final OperatorFactory
    private final WindowManager windowManager;
    private final Controller controller;

    public OperatorManager(WindowManager windowManager, Controller controller) {
        this.windowManager = windowManager;
        this.controller = controller;
    }

    public void execute(OperatorType<?> type) {
        Operator<?, ?> op = null;
        this.controller.executeNewTransaction(this.windowManager.getHistoryManager(), type.title, () -> op.execute());
    }

    public <T extends OperatorProperties> void execute(OperatorType<T> type, T props) {
        Operator<T, ?> op = null;
        this.controller.executeNewTransaction(this.windowManager.getHistoryManager(), type.title, () -> op.execute());
    }

    public <T extends OperatorProperties> void invoke(OperatorType<T> type, T props) {

    }
}
