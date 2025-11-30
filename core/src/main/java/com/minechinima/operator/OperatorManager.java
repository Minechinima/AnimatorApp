package com.minechinima.operator;

import com.minechinima.controller.*;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ChangeEvent;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.ReleaseConfirmHotKey;
import com.minechinima.operator.config.modal.*;
import com.minechinima.operator.config.operator.OperatorConfigGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.history.HistoryManager;
import com.minechinima.operator.modal.*;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.UIOperatorType;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.operator.ot.commands.flags.OPReturnValue;
import com.minechinima.operator.ot.commands.flags.OpExecuteFlag;
import com.minechinima.operator.ot.commands.flags.OpInvokeFlag;
import com.minechinima.operator.ot.commands.flags.OpModalFlag;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.elements.UIHandlerDummy;
import com.minechinima.ui.elements.UIModal;
import com.minechinima.ui.elements.window.UIRegion;
import com.minechinima.ui.elements.window.UISpace;
import com.minechinima.utils.Pair;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * Manages running operators, handles triggering them and state changes.
 * <br><br>
 * This is the only place an Operator can be instantiated and triggered correctly.
 * <br><br>
 * Central management of user interaction. It is a simplified interface for user interaction and more complex
 * underlying systems like history management and transaction management.
 * It will not throw exceptions for illegal arguments or other wrong code usages,
 * it will display errors directly in the UI.
 * @implNote Important note about the type safety of the generics and casting:
 *           The type safety of the context object is ensured by the public interface of this class.
 *           You can either pass an {@link OperatorType} with C as context or a {@link UIOperatorType},
 *           of which the context class is already known to be {@link UIOperatorContext}.
 *           This means that in any place you can check if the type is not instance of {@link UIOperatorType} and
 *           infer C as the context type, as this is the only possible way of passing an operatortype into this class.
 */
public class OperatorManager<C extends IContext> implements IKeyMapEventListener<KeyMapEvent> {
    private final UserFunctionalityConfigGroupStruct<C> config;
    @Nullable
    private UIManager<C> uiManager;
    /**
     * Do not expose it, as it would violate layered architecture.
     */
    private final ControllerManager controller;

    @Nullable
    private RunningModal<?, ?> modal;
    /**
     * Cache of keys that are pressed during modal and should now be blocked until they are released.
     */
    private Set<Pair<KeyMapEvent, ICachedEventBlocker>> cachedBlockedEvents = new HashSet<>();
    /**
     * Contains the logic which events to block.
     * Each event class gets a set of strategies how to block that cached event.<br>
     * Key is the class of the cached event in {@link #cachedBlockedEvents}.
     */
    private Map<Class<? extends KeyMapEvent>, Set<ICachedEventBlocker>> blockStrategies = new HashMap<>();

    /**
     * Operator that is controlled by a UI popup and blocks other operators.
     * Only UI Operators can be executed while this is running.
     */
    @Nullable
    private RunningUIMode<?, ?, ?, ?> runningInUIMode;

    private final Logger logger;
    /**
     * Check if any kind of execution is currently performed to prevent nested calls.
     * Nesting OperatorManager.execute/invoke... calls is not supported, as the chronological order
     * of the operator with the controller commands is not possible.
     */
    private boolean isExecuting;
    /**
     * true during the execution of {@link #onUIPropertyChange(BaseStructure)}
     */
    private boolean isUIPropChange;
    private final HistoryManager historyManager;
    /**
     * The current context on which the operators are performed.
     * This can be null when the context is currently built and is not available.
     * While this is null the invoke and execute Operators should be cached.
     * <br><br>
     * Example: The UI states for the UI Context are only known when the UI finished rendering.
     */
    @Nullable
    private C currentContext;
    /**
     * Cached operator for execution that were called while {@link #currentContext} was null.
     */
    private final Queue<QueueEntry> queue = new LinkedList<>();


    public OperatorManager(UserFunctionalityConfigGroupStruct<C> config,
                           HistoryManager historyManager, ControllerManager controller, Logger logger) {
        this(null, config, historyManager, controller, logger);
    }

    public OperatorManager(UIManager<C> uiManager, UserFunctionalityConfigGroupStruct<C> config,
                           HistoryManager historyManager, ControllerManager controller, Logger logger) {
        this.config = config;
        this.controller = controller;
        this.uiManager = uiManager;
        this.logger = logger;
        this.historyManager = historyManager;
    }

    /**
     * Register a strategy for blocking cached events that were triggered during executing an operator
     * in modal mode.
     * This is used to prevent operators being triggered accidentally after finishing an operator.
     * <br><br>
     * Example: User presses a key while an operator is in modal mode.
     * The user keeps the key down even after the operator finished.
     * The key should only trigger an operator again after it was released, therefore it needs to be blocked.
     * @param clazz
     * @param strategy
     * @param <T>
     */
    public <T extends KeyMapEvent> void registerEventBlockStrategy(Class<T> clazz, ICachedEventBlocker<T> strategy) {
        //TODO should we use inheritance of class type e.g. the strategy is also applied on subclasses of the event?
        Set<ICachedEventBlocker> strategies;
        if ((strategies = this.blockStrategies.get(clazz)) == null) {
            this.blockStrategies.put(clazz, strategies = new HashSet<>());
        }
        strategies.add(strategy);
    }

    public void setUIManager(UIManager<C> uiManager) {
        this.uiManager = uiManager;
    }

    public UserFunctionalityConfigGroupStruct<C> getConfig() {
        return this.config;
    }

    @Nullable
    public C getCurrentContext() {
        return this.currentContext;
    }

    /**
     * Set the current context on which the operators are performed.
     * @param context
     */
    public void setCurrentContext(C context) {
        boolean wasNull = this.currentContext == null;
        this.currentContext = context;

        if (wasNull && this.currentContext != null) {
            QueueEntry entry;
            while ((entry = this.queue.poll()) != null) {
                switch (entry.method) {
                    case INVOKE -> this._invokeOT(entry.type, entry.props);
                    case EXECUTE -> this._executeOT(entry.type, entry.props);
                }
            }
        }
    }

    public void resetCurrentContext() {
        this.currentContext = null;
    }

    /**
     * @return true if an operator is running in modal mode.
     */
    public boolean isModalMode() {
        return this.modal != null;
    }

    /**
     * @return true if an operator is running in ui mode.
     */
    public boolean isUIMode() {
        return this.runningInUIMode != null;
    }

    /**
     * Only executes a transaction when there is no modal running. If you want a continuous transaction, you need to
     * invoke a modal operator. Executing database transactions directly is only for simple one time commands so the UI
     * can easily change data.
     * <b>Does not commit when there is a modal operator running! See {@link #isModalMode()}</b>
     * @param title
     * @param commands
     * @param pushUndo
     * @return
     */
    public boolean executeTransaction(String title, Queue<IControllerCommand> commands, boolean pushUndo) {
        if (this.modal != null || this.controller.isActive()) return false;
        this.controller.startTransaction();

        if (this.controller.writeToTransaction(commands).state() != ControllerResult.State.SUCCESS) {
            return false;
        }

        ControllerResult result = this.controller.commitTransaction(title);
        if (result.state() != ControllerResult.State.SUCCESS) return false;
        if (pushUndo) this.historyManager.pushUndo(result.transaction());
        return true;
    }

    public boolean executeTransaction(String title, IControllerCommand command, boolean pushUndo) {
        return this.executeTransaction(title, ControllerUtils.asQueue(command), pushUndo);
    }

    /**
     * @return true if an undo step can be performed.
     */
    public boolean canUndo() {
        return this.modal == null && !this.controller.isActive();
    }

    /**
     * Undo the last operator.
     */
    public void undo() {
        if (!this.canUndo()) {
            return;
        }

        HistoryManager.UndoRedoStep undoStep = this.historyManager.popUndo(this.controller.getDataTree().getRoot());
        if (undoStep == null) return;
        ControllerResult result = this.controller.executeTransaction("Undo: ", ControllerUtils.asQueue(undoStep.getCommand()));

        if (result.state() == ControllerResult.State.ERROR) {
            System.out.println("Error " + result.userMessage());
            if (result.exception() != null) {
                result.exception().printStackTrace();
            }
            return;
        }

        this.historyManager.pushRedo(result.transaction(), undoStep);
    }

    /**
     *
     * @return true if a redo step can be performed.
     */
    public boolean canRedo() {
        return this.modal == null && !this.controller.isActive();
    }

    /**
     * Redo the last undo step
     */
    public void redo() {
        if (!this.canRedo()) {
            return;
        }

        HistoryManager.UndoRedoStep redoStep = this.historyManager.popRedo(this.controller.getDataTree().getRoot());
        if (redoStep == null) return;
        ControllerResult result = this.controller.executeTransaction("Redo: ", ControllerUtils.asQueue(redoStep.getCommand()));

        if (result.state() == ControllerResult.State.ERROR) {
            return;
        }

        this.historyManager.pushUndo(result.transaction(), redoStep);
    }

    /* +-----------------+
     * | EXECUTE METHODS |
     * +-----------------+ */



    public <OP extends IOperator<C>,
            OT extends OperatorType<C, OP>>
    void execute(OT type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<C, P> & IOperator<C>,
            OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    public <OP extends IOperator<UIOperatorContext>,
            OT extends UIOperatorType<OP>>
    void execute(OT type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<UIOperatorContext, P> & IOperator<UIOperatorContext>,
            OT extends UIOperatorType<OP> & IOTPropertyProvider<OP, P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    /**
     * @param props if this is not null it must have passed through the public API {@link #execute(OperatorType, OperatorPropertiesGroupStruct)},
     *              which ensures that OperatorType implements {@link IOTPropertyProvider} and therefore casting is safe.
     */
    private <CTX extends IContext,
            OP extends IOperator<CTX>,
            OPP extends IPropertyOperator<CTX, P> & IOperator<CTX>,
            P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX, OPP> & IOTPropertyProvider<OPP, P>,
            OT extends OperatorType<CTX, OP>>
    void _executeOT(OT type, @Nullable P props) {
        if (this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }

        if (this.controller.isActive() || this.modal != null ||
                (this.runningInUIMode != null && !(type instanceof UIOperatorType))) {
            return;
        }

        if (this.currentContext == null) {
            this.queue.add(new QueueEntry(type, props, Method.EXECUTE));
            return;
        }

        this.isExecuting = true;
        try {
            if (!type.poll(this.getContextForType(type))) return;
            this._execute(type, props == null ? type.getOperator() : (OP) ((OTP) type).getOperator(props));
        } catch (Exception e) {
            this.logger.info(e);
        } finally {
            this.isExecuting = false;
        }
    }

    private <CTX extends IContext,
            OP extends IOperator<CTX>>
    void _execute(OperatorType<CTX, OP> type, OP op) {
        if (op == null) return;

        final CTX ctx = this.getContextForType(type);

        /*
         * Cancel all other possible running operators if the executed operator is cancelled or fails.
         * Reasoning: Executing can happen while another operator is running. When another operator is running
         * the history steps will be combined, but when one fails the history and data integrity might be
         * endangered.
         */
        try (op) {
            OPReturnValue<OpExecuteFlag> result = op.execute(ctx);
            switch (result.flag()) {
                case FINISHED -> {
                    this.handleWritingTransaction(type, op, ctx, result.commands(), !this.controller.isActive());
                }
            }
        } catch (Exception e) {
            /* not a UserException means the operator did not terminate itself */
            if (!(e instanceof UserException)) op.cancel();
            this.logger.info(e);
            e.printStackTrace();
        }
    }



    /* +----------------+
     * | INVOKE METHODS |
     * +----------------+ */



    public <OP extends IOperator<C>,
            OT extends OperatorType<C, OP>>
    void invoke(OT type) {
        this._invokeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<C, P> & IOperator<C>,
            OT extends OperatorType<C, OP> & IOTPropertyProvider<OP, P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    public <OP extends IOperator<UIOperatorContext>,
            OT extends UIOperatorType<OP>>
    void invoke(OT type) {
        this._invokeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<UIOperatorContext, P> & IOperator<UIOperatorContext>,
            OT extends UIOperatorType<OP> & IOTPropertyProvider<OP, P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    /**
     * @return true if the operator invoke method was called
     */
    private <CTX extends IContext,
            OP extends IOperator<CTX>,
            P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX, OP>>
    boolean _invokeOT(OT type, @Nullable P props) {
        return this._invokeOT(type, props, null);
    }

    /**
     * @return true if the operator invoke method was called
     */
    private <CTX extends IContext,
            OP extends IOperator<CTX>,
            OPP extends IPropertyOperator<CTX, P> & IOperator<CTX>,
            P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX, OPP> & IOTPropertyProvider<OPP, P>,
            OT extends OperatorType<CTX, OP>>
    boolean _invokeOT(OT type, @Nullable P props, @Nullable OperatorConfigGroupStruct<CTX> invokedConfig) {
        if (this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }

        if (this.modal != null ||
                (this.runningInUIMode != null && !(type instanceof UIOperatorType))) {
            return false;
        }

        if (this.currentContext == null) {
            this.queue.add(new QueueEntry(type, props, Method.INVOKE));
            return true;
        }

        this.isExecuting = true;
        try {
            if (!type.poll(this.getContextForType(type))) return false;
            return this._invoke(type, props == null ? type.getOperator() : (OP) ((OTP) type).getOperator(props), invokedConfig);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            this.isExecuting = false;
        }
    }

    /**
     * Conditions for invoking:
     * <ul>
     *     <li>No Operator is in Modal Mode</li>
     *     <li>If an operator is in UI Mode only a {@link UIOperatorType} can be invoked to interact with the UI of the ui mode operator.</li>
     * </ul>
     * @return true if the operator invoke method was called.
     */
    private <CTX extends IContext,
            OP extends IOperator<CTX>>
    boolean _invoke(OperatorType<CTX, OP> type, OP op, @Nullable OperatorConfigGroupStruct<CTX> invokedConfig) {
        if (op == null) {
            return false;
        }

        final CTX currentCTX = this.getContextForType(type);

        /*
         * Invoking an operator while another is running only happens when
         * an operator is running in UI mode and user tries to interact with UI.
         * In this case cancel or failure of operator should not cancel the UI mode operator.
         */
        OPReturnValue<OpInvokeFlag> result = null;
        /*
         * Some flags for the error handling code to correctly call things.
         */
        boolean closed = false;
        boolean modal = false;
        try {
            result = op.invoke(currentCTX);
            final Queue<IControllerCommand> commands = result.commands();

            switch (result.flag()) {
                case CANCELLED -> {
                    closed = true;
                    op.close();
                }
                case FINISHED, MODAL_RUNNING -> {
                    boolean runAsModal = result.flag() == OpInvokeFlag.MODAL_RUNNING && op instanceof IModalOperator;
                    this.handleWritingTransaction(type, op, currentCTX, commands, result.flag() == OpInvokeFlag.FINISHED);
                    if (runAsModal) {
                        if (!this.controller.isActive()) this.controller.startTransaction();
                        modal = true;
                        this.modal = new RunningModal(type, (IModalOperator) op, invokedConfig);
                    } else {
                        closed = true;
                        op.close();
                    }
                }
                case RUN_IN_UI -> {
                    /*
                     * atm running a UIOperatorType in ui mode makes no sense
                     * as a UIOperator might be invoked while one is already in UI mode...
                     */
                    //TODO ui mode very unfinished. How do we determine where UI modal spawns even...?
                    if (!(type instanceof UIOperatorType) && type instanceof IOTPropertyProvider otp
                            && op instanceof IPropertyOperator propOp) {
                        //this.handleWritingTransaction(type, op, currentCTX, commands, false);
                        //this._uimode(otp, propOp, currentCTX);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.info(e);
            if (!(e instanceof UserException)) {
                op.cancel();
            }
            if (!closed) op.close();
            if (modal) this.modal = null;
        }

        return true;
    }








    private <CTX extends IContext, P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX, OP> & IOTPropertyProvider<OP, P>,
            OP extends IPropertyOperator<CTX, P>>
    void _uimode(OT type, OP op, CTX currentContext) {
        if (this.runningInUIMode != null) return;

        //TODO possible flaw: current hovered window and app context data might not align
        if (!this.uiManager.getActiveWindow().canOpenModal()) {
            return;
        }

        UIModal<C> ui = op.propUI();
        if (ui == null) {
            Collection<IUIHandler<P>> handlers = this.uiManager.handlerRegistry.getUnnamedHandlersFor(op.props());
            ui = new UIModal<C>(confirm -> {
                if (!confirm) {
                    op.cancel();
                } else {
                    this.commitUIMode();
                }
                this.runningInUIMode = null;
            }, new UIHandlerDummy<>(op.props(), handlers));
        }

        EventBus busManager = this.controller.getDataTree().getRoot().getEventBus();
        Object subscriber = new Object() {
            @SubscribeEvent(type = SubscribeEvent.Type.ALL)
            public void onChange(ChangeEvent<?> change) {
                if (!OperatorManager.this.onUIPropertyChange(change.getTrigger())) {
                    busManager.unsubscribe(this);
                    type.removeRuntimeProps(op.props());
                }
            }
        };

        type.addRuntimeProps(op.props());
        try {
            op.props().getEventBus().subscribe(subscriber);
            this.runningInUIMode = new RunningUIMode<>(type, op, subscriber);
            this.uiManager.getActiveWindow().openModalAt(ui, 0, 0);
        } catch (Exception e) {
            type.removeRuntimeProps(op.props());
            throw e;
        }
    }

    private void commitUIMode() {
        RunningUIMode uiMode = this.runningInUIMode;
        this.controller.commitTransaction(this.getTitle(uiMode.type, uiMode.op, this.getContextForType(uiMode.type)));
        this.runningInUIMode.op.close();
        this.runningInUIMode = null;
    }

    private void cancelUIMode() {
        this.runningInUIMode.op.cancel();
        this.setUIModalNull();
    }

    @NotNull
    private <CTX extends IContext> CTX getContextForType(OperatorType<CTX, ?> type) {
        return (CTX) (type instanceof UIOperatorType ? this.currentContext.getUIContext() : this.currentContext);
    }

    /**
     * Closing method for modal operators.
     */
    private void setModalNull() {
        this.modal.op.close();
        this.modal = null;
    }

    /**
     * Closing method for UI controlled operators.
     */
    private void setUIModalNull() {
        this.runningInUIMode.op.close();
        this.runningInUIMode = null;
    }

    private <CTX extends IContext, P extends OperatorPropertiesGroupStruct,
            OP extends IPropertyOperator<CTX, P>,
            OT extends OperatorType<CTX, OP> & IOTPropertyProvider<OP, P>>
    boolean onUIPropertyChange(BaseStructure changed) {
        if (this.runningInUIMode == null || this.currentContext == null) return false;
        if (this.isUIPropChange || this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }

        this.isUIPropChange = true;
        this.isExecuting = true;

        RunningUIMode uimode = this.runningInUIMode;
        try {
            final CTX currentCTX = this.getContextForType((OT) uimode.type);
            /* onUIPropChange() could theoretically close the ui model directly and therefore close ui mode */
            OPReturnValue<OpExecuteFlag> retval = uimode.op.onUIPropChange(currentCTX, changed);
            if (this.runningInUIMode == null) {
                this.logger.info("UI Mode was prematurely closed in onUIPropChange() method.");
                return true;
            }

            final OpExecuteFlag flag = retval.flag();
            final Queue<IControllerCommand> commands = retval.commands();

            switch (flag) {
                case CANCELLED -> {
                    return false;
                }
                case FINISHED -> {
                    if (commands != null) this.handleWritingTransaction(uimode.type, uimode.op, currentCTX, commands, false);
                    return true;
                }
            }
        } catch (Exception e) {
            if (!(e instanceof UserException)) uimode.op.cancel();
            this.logger.error(e);
            this.runningInUIMode = null;
            return false;
        } finally {
            this.isUIPropChange = false;
            this.isExecuting = false;
        }

        return false;
    }

    private void _modal(KeyMapEvent event) {
        if (this.modal == null) return;

        if (this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }

        final IModalOperator op = this.modal.op;
        final OperatorType ot = this.modal.type;
        this.isExecuting = true;
        boolean closed = false;
        try {
            IContext context = this.getContextForType(this.modal.type);

            final OPReturnValue<IModalOperator.Modal> retval = op.modal(context, event);
            final OpModalFlag flag = retval.flag().flag();
            final Queue<IControllerCommand> commands = retval.commands();

            switch (flag) {
                case CANCELLED -> {
                    if (this.controller.isActive()) this.controller.abortTransaction();
                    this.modal = null;
                    closed = true;
                    op.close();
                }
                case FINISHED -> {
                    this.handleWritingTransaction(ot, op, context, commands, true);
                    this.modal = null;
                    closed = true;
                    op.close();
                }
                case MODAL_RUNNING -> {
                    this.handleWritingTransaction(ot, op, context, commands, false);
                }
            }
        } catch (Exception e) {
            if (this.controller.isActive()) this.controller.abortTransaction();
            /* UserException would come from the operator method itself, meaning operator is not cancelled by external systems */
            if (!(e instanceof UserException)) {
                op.cancel();
            }
            if (!closed) op.close();
            this.modal = null;
            this.logger.error(e);
        } finally {
            this.isExecuting = false;
        }
    }





    /* +-----------+
     * | UTILITIES |
     * +-----------+ */

    public void confirmModal() {
        this.commitModal(null);
    }

    private void commitModal(@Nullable KeyMapEvent event) {
        IContext context = this.getContextForType(this.modal.type);
        IModalOperator op = this.modal.op;
        try {
            OPReturnValue<OpExecuteFlag> ret = op.confirmModal(context, event);
            if (ret.flag() == OpExecuteFlag.CANCELLED) {
                this.controller.abortTransaction();
                return;
            }

            if (ret.commands() != null) {
                this.handleWritingTransaction((OperatorType) this.modal.type, (IModalOperator) this.modal.op,
                        context, ret.commands(), true);
            } else {
                this.handleCommitTransaction((OperatorType) this.modal.type, (IModalOperator) this.modal.op, context);
            }
        } catch (UserException e) {

        } finally {
            this.setModalNull();
        }
    }

    @NotNull
    private <CTX extends IContext, OP extends IOperator<CTX>> String getTitle(OperatorType<CTX, OP> type, OP op, CTX ctx) {
        if (!(op instanceof ITitleProvider titleProvider)) {
            return type.title;
        }

        String title = titleProvider.getTitle(ctx);
        return title == null ? type.title : title;
    }

    public void cancelModal() {
        if (this.modal == null) return;
        if (this.controller.isActive()) this.controller.abortTransaction();
        this.modal.op.cancel();
        this.setModalNull();
    }

    /**
     * Starts transaction if controller is not active.<br>
     * Cancels Operator on failure.<br>
     * Handles errors and UI presentation of errors.<br>
     * @return true on success.
     */
    private <CTX extends IContext, OP extends IOperator<CTX>>
    void handleWritingTransaction(OperatorType<CTX, OP> type, OP op, CTX current,
                                     Queue<IControllerCommand> commands, boolean commit) throws UserException {
        if (commands.isEmpty() && commit) {
            this.handleCommitTransaction(type, op, current);
            return;
        }
        if (!this.controller.isActive()) this.controller.startTransaction();

        ControllerResult result = this.controller.writeToTransaction(commands);

        if (result.state() == ControllerResult.State.ERROR) {
            if (result.exception() != null) {
                throw new UserException(result.userMessage(), result.exception());
            } else {
                throw new UserException(result.userMessage());
            }
        }

        if (commit) {
            this.handleCommitTransaction(type, op, current);
        }
    }

    /**
     * Handles committing a transaction, if controller is active and pushing to history.<br>
     * Handles errors and UI presentation of errors.<br>
     * Calls {@link IOperator#cancel()} on error.
     * @return true if it was successful
     */
    private <CTX extends IContext, OP extends IOperator<CTX>>
    void handleCommitTransaction(OperatorType<CTX, OP> type, OP op, CTX current) throws UserException {
        Transaction transaction = null;
        if (this.controller.isActive()) {
            ControllerResult result = this.controller.commitTransaction(this.getTitle(type, op, current));
            transaction = result.transaction();
            if (result.state() == ControllerResult.State.ERROR) {
                if (result.exception() != null) {
                    throw new UserException(result.userMessage(), result.exception());
                } else {
                    throw new UserException(result.userMessage());
                }
            }
        }

        OperatorPropertiesGroupStruct props = op instanceof IPropertyOperator<?,?> propOp ? propOp.props() : null;
        if (type.pushUndo()) {
            if (op instanceof IUndoOperator undoOperator) {
                this.historyManager.pushUndo(transaction, type, undoOperator, props);
            } else {
                this.historyManager.pushUndo(transaction, type, props);
            }
        }
    }

    private void runModalFunctions(KeyMapEvent event) {
        if (this.currentContext == null) return;

        Iterator<RunningModal.FunctionsRelease<?>> it = this.modal.functionsWaitingRelease.iterator();
        while (it.hasNext()) {
            RunningModal.FunctionsRelease pair = it.next();
            if (pair.config.onEvent(event)) {
                IModalFunctionReleasableCommand command = pair.command;
                command.onReleaseEvent(this, this.currentContext, this.modal.op);
                it.remove();
                return;
            }
        }

        for (ModalFunctionHotKeyConfig<C> cfg : this.config.modalFunctions.getChildren()) {
            if (cfg.hotKeyConfigSelector.get().onEvent(event)) {
                ModalFunctionConfig modifier = cfg.modalFunction.get();
                IModalFunctionCommand command = modifier.factory();

                /*
                 * ATTENTION: any states could be modified by this and need to be rechecked...
                 */
                command.onEvent(this, this.currentContext, this.modal.op);
                if (this.modal == null) return;

                /*
                 * Check if the Modifier has the capabilities to be released
                 * and if the modifier config has a modal release confirm module and a hotkey set for
                 * the release confirm.
                 */
                if (modifier instanceof ModalFunctionReleasableConfig<?>
                        && command instanceof IModalFunctionReleasableCommand<?,?> releasableCommand
                        && cfg.modalConfirmConfig.get() != null
                        && cfg.modalConfirmConfig.get().hotKeyConfigSelector.get() != null
                        && !this.isModalFunctionWaiting(releasableCommand)) {
                    this.modal.functionsWaitingRelease.add(
                            new RunningModal.FunctionsRelease<>(cfg.modalConfirmConfig.get().hotKeyConfigSelector.get(), releasableCommand));
                }
            }
        }
    }

    private boolean isModalFunctionWaiting(IModalFunctionReleasableCommand command) {
        for (RunningModal.FunctionsRelease func : this.modal.functionsWaitingRelease) {
            if (func.command.getClass().equals(command.getClass())) {
                return true;
            }
        }

        return false;
    }

    private void runPropertyModifiers(KeyMapEvent event) {
        if (!(this.modal.op instanceof IPropertyOperator propOp)) return;

        Iterator<RunningModal.ModifierRelease<?>> it = this.modal.modifiersWaitingRelease.iterator();
        while (it.hasNext()) {
            RunningModal.ModifierRelease pair = it.next();
            if (pair.config.onEvent(event)) {
                ControllerResult result = this.controller.writeToTransaction(pair.command.onReleaseEvent(propOp.props()));

                if (result.state() != ControllerResult.State.SUCCESS) {
                    this.cancelModal();
                    return;
                }

                it.remove();
                return;
            }
        }

        List<PropertyModifierHotKeyConfig<OperatorPropertiesGroupStruct>> propertyModalKeymap = this.config.propModifiers.getConfigs(propOp.props());
        for (PropertyModifierHotKeyConfig<?> cfg : propertyModalKeymap) {
            if (cfg.hotKeyConfigSelector.get().onEvent(event)) {
                PropertyModifierConfig modifier = cfg.modifier.get();
                IPropertyModifierCommand command = modifier.factory();

                ControllerResult result = this.controller.writeToTransaction(command.modify(propOp.props()));

                if (result.state() != ControllerResult.State.SUCCESS) {
                    this.cancelModal();
                    return;
                }

                /*
                 * Check if the Modifier has the capabilities to be released
                 * and if the modifier config has a modal release confirm module and a hotkey set for
                 * the release confirm.
                 */
                if (modifier instanceof PropertyModifierReleaseableConfig<?> releaseableConfig
                        && command instanceof IPropertyModifierReleasableCommand<?,?> releasableCommand
                        && cfg.modalConfirmConfig.get() != null
                        && cfg.modalConfirmConfig.get().hotKeyConfigSelector.get() != null
                        && !this.isModifierWaiting(releasableCommand)) {
                    this.modal.modifiersWaitingRelease.add(
                            new RunningModal.ModifierRelease<>(cfg.modalConfirmConfig.get().hotKeyConfigSelector.get(), releasableCommand));
                }
                break;
            }
        }
    }

    /**
     * Check if the modifier command was already triggered and waits for a release
     * @param command
     * @return
     */
    private boolean isModifierWaiting(IPropertyModifierReleasableCommand command) {
        for (RunningModal.ModifierRelease mod : this.modal.modifiersWaitingRelease) {
            if (mod.command.getClass().equals(command.getClass())) {
                return true;
            }
        }

        return false;
    }

    private boolean runModalEventLogic(KeyMapEvent event) {
        if (this.modal == null) return false;

        /* check if the given event should confirm the operator */
        final ReleaseConfirmHotKey.Config modalConfirmConfig;
        final HotKeyConfigGroupStruct modalConfirmHotKey;
        if (this.modal.config != null
                && (modalConfirmConfig = this.modal.config.modalConfirmConfig.get()) != null
                && (modalConfirmHotKey = modalConfirmConfig.hotKeyConfigSelector.get()) != null) {
            if (modalConfirmConfig.confirmOnRelease.get() && modalConfirmHotKey.onEvent(event)) {
                this.commitModal(event);
                return true;
            }
        }

        try {
            this.runModalFunctions(event);
        } catch (Exception e) {
            e.printStackTrace();
            this.cancelModal();
            return true;
        }

        /* modal functions could commit or cancel current operator,
         * return true as it means the modal functions did something. */
        if (this.modal == null) return true;

        try {
            this.runPropertyModifiers(event);
        } catch (Exception e) {
            e.printStackTrace();
            this.cancelModal();
            return true;
        }

        if (this.modal == null) return true;

        this._modal(event);
        return true;
    }

    public boolean isEventBlocked(KeyMapEvent event) {
        Iterator<Pair<KeyMapEvent, ICachedEventBlocker>> it = this.cachedBlockedEvents.iterator();

        /*
         * Go through the cached events and check for each if a strategy for the cached event blocks the incoming event.
         */
        boolean result = false;
        while (it.hasNext()) {
            final Pair<KeyMapEvent, ICachedEventBlocker> cached = it.next();
            if (cached.getB().clearCachedEvent(cached.getA(), event)) {
                it.remove();
                /* call the clearCachedEvent on every strategy to avoid memory leaks */
                result = true;
            } else if (cached.getB().isEventBlocked(cached.getA(), event)) {
                result = true;
            }
        }

        if (result) return true;

        if (this.isModalMode()) {
            /*
             * test if the event should be cached when it was not blocked already.
             */
            Set<ICachedEventBlocker> blockStrategies;
            if ((blockStrategies = this.blockStrategies.get(event.getClass())) != null) {
                for (ICachedEventBlocker blockStrategy : blockStrategies) {
                    if (blockStrategy.cacheEvent(event)) {
                        this.cachedBlockedEvents.add(new Pair<>(event, blockStrategy));
                        return true;
                    }
                }
            }
        }

        return false;
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        if (this.currentContext == null) return false;

        //TODO operator type switching and Operator needs to receive previous properties so the previous operator could influence the next

        if (this.runModalEventLogic(event) | this.isEventBlocked(event)) {
            return true;
        }

        /*
         * No Modal operator running, try and trigger?
         */
        UISpace spaceType = this.currentContext.getUIContext().currentHoveredUISpace();
        UIRegion regionType = this.currentContext.getUIContext().currentHoveredUIRegion();
        if (this.modal == null && spaceType != null) {
            if (regionType == null) {
                for (OperatorConfigGroupStruct<C> config : this.config.getGlobalConfigsForUISpace(spaceType.getClass())) {
                    if (this.triggerOperatorConfig(config, event)) return true;
                }

                for (OperatorConfigGroupStruct<C> config : this.config.getGlobalConfigs()) {
                    if (this.triggerOperatorConfig(config, event)) return true;
                }
            } else {
                for (OperatorConfigGroupStruct<C> config : this.config.getConfigsForUISpaceRegion(spaceType.getClass(), regionType.getClass())) {
                    if (this.triggerOperatorConfig(config, event)) return true;
                }

                for (OperatorConfigGroupStruct<C> config : this.config.getGlobalConfigsForUISpace(spaceType.getClass())) {
                    if (this.triggerOperatorConfig(config, event)) return true;
                }

                for (OperatorConfigGroupStruct<C> config : this.config.getGlobalConfigs()) {
                    if (this.triggerOperatorConfig(config, event)) return true;
                }
            }
        }

        return false;
    }

    private boolean triggerOperatorConfig(OperatorConfigGroupStruct<C> config, KeyMapEvent event) {
        HotKeyConfigGroupStruct hotkeyConfig = config.hotKeyConfigSelector.get();
        if (hotkeyConfig == null || !hotkeyConfig.onEvent(event)) return false;
        OperatorType<C, ?> ot;
        if ((ot = config.operatorType.get()) == null) return false;
        if (ot instanceof IOTPropertyProvider<?, ?> provider) {
            OperatorPropertiesGroupStruct newProps = provider.createProps();
            if (config.props.getProps() != null) newProps.copyFrom(config.props.getProps());
            return this._invokeOT(ot, newProps, config);
        } else {
            return this._invokeOT(ot, null, config);
        }
    }

    /**
     * Every data / command that is needed during running a modal operator. This represents on modal operator process.
     * @param type
     * @param op
     * @param modifiersWaitingRelease set of modifiers that await a release event.
     * @param <CTX>
     */
    protected record RunningModal<CTX extends IContext, OP extends IModalOperator<CTX>>
            (OperatorType<CTX, OP> type, OP op,
             @Nullable OperatorConfigGroupStruct<CTX> config,
             Set<ModifierRelease<?>> modifiersWaitingRelease,
             Set<FunctionsRelease<?>> functionsWaitingRelease)
    {
        public RunningModal(OperatorType<CTX, OP> type, OP op, @Nullable OperatorConfigGroupStruct<CTX> config) {
            this(type, op, config, new HashSet<>(), new HashSet<>());
        }

        protected record ModifierRelease<P extends OperatorPropertiesGroupStruct>
                (HotKeyConfigGroupStruct config, IPropertyModifierReleasableCommand<P, ?> command) {

        }

        protected record FunctionsRelease<CTX extends IContext>
                (HotKeyConfigGroupStruct config, IModalFunctionReleasableCommand<CTX, ?> command) {

        }
    }

    protected record RunningUIMode<CTX extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX, OP> & IOTPropertyProvider<OP, P>,
            OP extends IPropertyOperator<CTX, P>>
            (OT type, OP op, Object propSubscriber) {
    }

    protected enum Method {
        INVOKE,
        EXECUTE;
    }

    protected record QueueEntry(OperatorType type, OperatorPropertiesGroupStruct props, Method method) {

    }
}
