package com.minechinima.operator;

import com.minechinima.controller.Controller;
import com.minechinima.controller.ControllerResult;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.input.IOEventListener;
import com.minechinima.ui.input.events.IOEvent;
import com.minechinima.ui.input.events.IOKeyboardEvent;
import com.minechinima.ui.input.events.IOMouseEvent;

/**
 * Manages running operators, handles triggering them and state changes.
 * <br><br>
 * This is the only place an Operator can be instantiated and triggered correctly.
 */
public class OperatorManager<C extends OperatorContext> implements IOEventListener {
    private final OperatorFactory<C> factory;
    private final OperatorConfiguration<C> config;
    private final UIManager<C> uiManager;
    /**
     * Do not expose it, as it would violate layered architecture.
     */
    private final Controller controller;

    public OperatorManager(UIManager<C> uiManager, OperatorFactory<C> factory, OperatorConfiguration<C> config, Controller controller) {
        this.uiManager = uiManager;
        this.config = config;
        this.controller = controller;
        this.factory = factory;
    }

    public <T extends OperatorProperties> void execute(OperatorType<C, T> type) {
        if (!this.factory.hasType(type)) return;

        final UIWindow<C> hovered;
        final Operator<T, C> op;

        if ((hovered = this.uiManager.getCurrentlyHoveredWindow()) == null
                || (op = this.factory.createOperator(type)) == null) {
            return;
        }

        this.uiManager.setActiveWindow(hovered);

        ControllerResult result = this.controller.executeNewTransaction(this.uiManager.getHistoryManager(),
                type.title,
                () -> op.execute(hovered.context));
        System.out.println(result.userMessage());
        if (result.exception() != null)
        result.exception().printStackTrace();
    }

    public <T extends OperatorProperties> void execute(OperatorType<C, T> type, T props) {
        if (!this.factory.hasType(type)) return;

        final UIWindow<C> hovered;
        final Operator<T, C> op;

        if ((hovered = this.uiManager.getCurrentlyHoveredWindow()) == null
                || (op = this.factory.createOperator(type, props)) == null) {
            return;
        }

        ControllerResult result = this.controller.executeNewTransaction(this.uiManager.getHistoryManager(),
                type.title,
                () -> op.execute(hovered.context));
        System.out.println(result.userMessage());
    }

    public <T extends OperatorProperties> void invoke(OperatorType<C, T> type, T props) {

    }

    public void undo() {
        this.uiManager.getHistoryManager().undo(this.controller, this.controller.getDataTree().getRoot());
    }

    public void undo(BaseValueHierarchy<?> relative) {
        this.uiManager.getHistoryManager().undo(this.controller, relative);
    }

    public void redo(BaseValueHierarchy<?> relative) {
        this.uiManager.getHistoryManager().redo(this.controller, relative);
    }

    public void redo() {
        this.uiManager.getHistoryManager().redo(this.controller, this.controller.getDataTree().getRoot());
    }

    @Override
    public void onIOEvent(IOEvent event) {

    }

    @Override
    public void onMouseIOEvent(IOMouseEvent event) {

    }

    @Override
    public void onKeyIOEvent(IOKeyboardEvent event) {
        OperatorType<C, ?> type;
        if ((type = config.keyConfig.get(event.key())) == null) {
            return;
        }
        this.execute(type);
    }
}
