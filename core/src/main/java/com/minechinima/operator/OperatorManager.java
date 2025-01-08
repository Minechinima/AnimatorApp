package com.minechinima.operator;

import com.minechinima.controller.Controller;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.ot.AbstractOperatorType;
import com.minechinima.operator.ot.UIOperatorType;
import com.minechinima.operator.ot.commands.IOperator;
import com.minechinima.operator.ot.commands.IPropertyOperator;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.elements.UIWindow;
import com.minechinima.ui.input.IOEventListener;
import com.minechinima.ui.input.events.IOEvent;
import com.minechinima.ui.input.events.IOKeyboardEvent;
import com.minechinima.ui.input.events.IOMouseEvent;
import com.minechinima.utils.Pair;
import org.jetbrains.annotations.Nullable;

/**
 * Manages running operators, handles triggering them and state changes.
 * <br><br>
 * This is the only place an Operator can be instantiated and triggered correctly.
 */
public class OperatorManager<C extends OperatorContext> implements IOEventListener {
    private final OperatorFactory factory;
    private final OperatorConfiguration<C> config;
    @Nullable
    private UIManager<C> uiManager;
    /**
     * Do not expose it, as it would violate layered architecture.
     */
    private final Controller controller;

    private Pair<UIOperatorType, IOperator<UIOperatorContext>> runningUIOp;
    private Pair<AbstractOperatorType<C, ?>, IOperator<C>> runningAppOp;

    public OperatorManager(OperatorFactory factory, OperatorConfiguration<C> config, Controller controller) {
        this(null, factory, config, controller);
    }

    public OperatorManager(UIManager<C> uiManager, OperatorFactory factory, OperatorConfiguration<C> config, Controller controller) {
        this.config = config;
        this.controller = controller;
        this.factory = factory;
        this.uiManager = uiManager;
    }

    public void setUIManager(UIManager<C> uiManager) {
        this.uiManager = uiManager;
    }

    private boolean poll(AbstractOperatorType<?, ?> type) throws UserException {
        final UIWindow<C> hovered;

        if ((hovered = this.uiManager.getCurrentlyHoveredWindow()) == null) {
            return false;
        }

        if (type instanceof UIOperatorType uiOT) {
            return uiOT.poll(hovered.context.uiContext);
        } else {
            return ((AbstractOperatorType<C, ?>) type).poll(hovered.context);
        }
    }

    /*
     * EXECUTE
     */

    public <OT extends AbstractOperatorType<C, AbstractOperatorType<C, ?>>> void execute(OT type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorProperties,
            OT extends AbstractOperatorType<C, AbstractOperatorType<C, ?>> & AbstractOperatorType.PropertyProvider<P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    public void execute(UIOperatorType type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorProperties,
            OT extends UIOperatorType & AbstractOperatorType.PropertyProvider<P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    private <CTX,
            P extends OperatorProperties,
            OTP extends AbstractOperatorType<CTX, ?> & AbstractOperatorType.PropertyProvider<P>,
            OT extends AbstractOperatorType<CTX, ?>>
    void _executeOT(OT type, P props) {
        try {
            if (!this.poll(type)) return;
        } catch (UserException e) {
            e.printStackTrace();
        }

        this._execute(type, props == null ? this.factory.getOperator(type) : this.factory.getOperator((OTP) type, props));
    }

    private void _execute(AbstractOperatorType type, @Nullable IOperator op) {
        if (op == null) return;
        final UIWindow<C> hovered;

        if (op == null || (hovered = this.uiManager.getCurrentlyHoveredWindow()) == null) {
            return;
        }

        try {
            op.execute(hovered.context);
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    /*
     * INVOKE
     */

    public <P extends OperatorProperties,
            OT extends AbstractOperatorType<C, AbstractOperatorType<C, ?>> & AbstractOperatorType.PropertyProvider<P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    public <OT extends AbstractOperatorType<C, ?>> void invoke(OT type) {
        this._invokeOT(type);
    }

    public void invoke(UIOperatorType type) {
        this._invokeOT(type);
    }

    public <P extends OperatorProperties,
            OT extends UIOperatorType & AbstractOperatorType.PropertyProvider<P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    private <CTX> void _invokeOT(AbstractOperatorType<CTX, ?> type) {
        this._invoke(type, this.factory.getOperator(type));
    }

    private <CTX,
            P extends OperatorProperties,
            OTP extends AbstractOperatorType<CTX, ?> & AbstractOperatorType.PropertyProvider<P>,
            OT extends AbstractOperatorType<CTX, ?>>
    void _invokeOT(OT type, @Nullable P props) {
        this._invoke(type, props == null ? this.factory.getOperator(type) : this.factory.getOperator((OTP) type, props));
    }

    private void _invoke(AbstractOperatorType type, @Nullable IOperator op) {
        if (op == null) return;
        final UIWindow<C> hovered;

        if (op == null || (hovered = this.uiManager.getCurrentlyHoveredWindow()) == null) {
            return;
        }

        try {
            op.invoke(hovered.context);
        } catch (UserException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onIOEvent(IOEvent event) {

    }

    @Override
    public void onMouseIOEvent(IOMouseEvent event) {
        if (this.runningAppOp != null) {
            //on mouse movethis.runningAppOp.getB().modal();
        }
    }

    @Override
    public void onKeyIOEvent(IOKeyboardEvent event) {
        AbstractOperatorType<C, ?> type;
        if ((type = this.config.keyConfig.get(event.key())) == null) {
            return;
        }

        this.invoke(type);
    }
}
