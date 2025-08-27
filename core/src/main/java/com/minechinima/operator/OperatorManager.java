package com.minechinima.operator;

import com.minechinima.controller.ControllerManager;
import com.minechinima.controller.ControllerResult;
import com.minechinima.controller.ControllerUtils;
import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.input.hotkey.HotKeyConfigGroupStruct;
import com.minechinima.input.hotkey.HotKeyConfigRegistry;
import com.minechinima.input.keymap.IKeyMapEventListener;
import com.minechinima.input.keymap.events.KeyMapEvent;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.events.bus.EventBus;
import com.minechinima.model.events.bus.SubscribeEvent;
import com.minechinima.model.events.types.changes.ChangeEvent;
import com.minechinima.model.exceptions.ContractViolatedException;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.operator.config.modal.ModalFuncKeyConfigItemGroupStruct;
import com.minechinima.operator.config.operator.OperatorKeyConfigItemGroupStruct;
import com.minechinima.operator.config.UserFunctionalityConfigGroupStruct;
import com.minechinima.operator.config.modal.PropModifierKeyConfigItemGroupStruct;
import com.minechinima.operator.config.modal.PropModifierKeyConfigListStruct;
import com.minechinima.operator.history.HistoryManager;
import com.minechinima.operator.ot.OperatorType;
import com.minechinima.operator.ot.IOTPropertyProvider;
import com.minechinima.operator.ot.UIOperatorType;
import com.minechinima.operator.ot.commands.*;
import com.minechinima.ui.UIManager;
import com.minechinima.ui.annotation.IUIHandler;
import com.minechinima.ui.elements.UIHandlerDummy;
import com.minechinima.ui.elements.UIModal;
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
    private final OperatorFactory factory;
    private final UserFunctionalityConfigGroupStruct<C> config;
    @Nullable
    private UIManager<C> uiManager;
    /**
     * Do not expose it, as it would violate layered architecture.
     */
    private final ControllerManager controller;

    @Nullable
    private RunningModal<?> modal;
    /**
     * If {@link HotKeyConfigGroupStruct#hasReleaseEvent()} is true,
     * this will cache the hotkey, to block the event until it is released
     * to prevent accidentally continuously
     * triggering the operator.<br><br>
     *
     * Example of this:<br>
     * User drags ui slider, presses right click (while still holding drag key), operator
     * is aborted but now when the user moves again they will trigger the operator immediately again.
     */
    @Nullable
    private HotKeyConfigGroupStruct blockedModalEvent;

    /**
     * Operator that is controlled by a UI popup.
     */
    @Nullable
    private RunningUIMode<?, ?, ?> runningInUIMode;

    private final Logger logger;
    private final Class<C> contextClass;
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

    public OperatorManager(OperatorFactory factory, UserFunctionalityConfigGroupStruct<C> config,
                           HistoryManager historyManager, ControllerManager controller, Logger logger, Class<C> contextClass) {
        this(null, factory, config, historyManager, controller, logger, contextClass);
    }

    public OperatorManager(UIManager<C> uiManager, OperatorFactory factory, UserFunctionalityConfigGroupStruct<C> config,
                           HistoryManager historyManager, ControllerManager controller, Logger logger, Class<C> contextClass) {
        this.config = config;
        this.controller = controller;
        this.factory = factory;
        this.uiManager = uiManager;
        this.logger = logger;
        this.contextClass = contextClass;
        this.historyManager = historyManager;
    }

    public void setUIManager(UIManager<C> uiManager) {
        this.uiManager = uiManager;
    }

    public UserFunctionalityConfigGroupStruct<C> getConfig() {
        return this.config;
    }

    /**
     * @return true if an operator is running in modal mode.
     */
    public boolean isModalMode() {
        return this.modal != null;
    }

    /**
     * Writes to the currently running transaction or creates a new one.
     * This does not commit the transaction.
     * @param commands
     * @return true if it was successful.
     */
    public boolean writeToTransaction(Queue<IControllerCommand> commands) {
        if (!this.controller.isActive()) this.controller.startTransaction();
        return this.controller.writeToTransaction(commands).state() == ControllerResult.State.SUCCESS;
    }

    public boolean writeToTransaction(IControllerCommand command) {
        return this.writeToTransaction(ControllerUtils.asQueue(command));
    }

    public boolean executeTransaction(String title, Queue<IControllerCommand> commands, boolean pushUndo) {
        if (!this.writeToTransaction(commands)) return false;
        return this.commitTransaction(title, pushUndo);
    }

    public boolean executeTransaction(String title, IControllerCommand command, boolean pushUndo) {
        return this.executeTransaction(title, ControllerUtils.asQueue(command), pushUndo);
    }

    /**
     * Commits the current transaction.
     * <br><br>
     * <b>Does not commit when there is a modal operator running! See {@link #isModalMode()}</b>
     * @param pushUndo whether to push the transaction, if committed successfully, into the history.
     * @param title
     */
    public boolean commitTransaction(String title, boolean pushUndo) {
        if (this.modal != null || !this.controller.isActive()) return false;
        ControllerResult result = this.controller.commitTransaction(title);
        if (result.state() != ControllerResult.State.SUCCESS) return false;
        if (pushUndo) this.historyManager.pushUndo(result.transaction(), null, null);
        return true;
    }

    public boolean canUndo() {
        return this.modal == null && !this.controller.isActive();
    }

    public void undo() {
        if (!this.canUndo()) {
            return;
        }

        HistoryManager.UndoRedoStep undoStep = this.historyManager.popUndo(this.controller.getDataTree().getRoot());
        if (undoStep == null) return;
        ControllerResult result = this.controller.executeTransaction("Undo: ", ControllerUtils.asQueue(undoStep.command));

        if (result.state() == ControllerResult.State.ERROR) {
            System.out.println("Error " + result.userMessage());
            if (result.exception() != null) {
                result.exception().printStackTrace();
            }
            return;
        }

        this.historyManager.pushRedo(result.transaction(), undoStep);
    }

    public boolean canRedo() {
        return this.modal == null && !this.controller.isActive();
    }


    public void redo() {
        if (!this.canRedo()) {
            return;
        }

        HistoryManager.UndoRedoStep redoStep = this.historyManager.popRedo(this.controller.getDataTree().getRoot());
        if (redoStep == null) return;
        ControllerResult result = this.controller.executeTransaction("Redo: ", ControllerUtils.asQueue(redoStep.command));

        if (result.state() == ControllerResult.State.ERROR) {
            return;
        }

        this.historyManager.pushUndo(result.transaction(), redoStep);
    }

    /* +-----------------+
     * | EXECUTE METHODS |
     * +-----------------+ */



    public <OT extends OperatorType<C>> void execute(OT type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<C> & IOTPropertyProvider<P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    public void execute(UIOperatorType type) {
        this._executeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OT extends UIOperatorType & IOTPropertyProvider<P>>
    void execute(OT type, P props) {
        this._executeOT(type, props);
    }

    private <CTX extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX> & IOTPropertyProvider<P>,
            OT extends OperatorType<CTX>>
    void _executeOT(OT type, @Nullable P props) {
        if (this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }
        this.isExecuting = true;
        try {
            try {
                if (!this.poll(type)) return;
            } catch (UserException e) {
                //TODO
                this.logger.info(e);
                return;
            }

            this._execute(type, props == null ? this.factory.getOperator(type) : this.factory.getOperator((OTP) type, props));
        } finally {
            this.isExecuting = false;
        }
    }

    private <CTX extends IContext> void _execute(OperatorType<CTX> type, @Nullable IOperator<CTX> op) {
        if (op == null) return;
        final CTX ctx;

        if ((ctx = this.getCurrentContextForType(type)) == null) {
            //TODO
            this.logger.info("Could not execute operator as no context is given.");
            return;
        }

        /*
         * Cancel all other possible running operators if the executed operator is cancelled or fails.
         * Reasoning: Executing can happen while another operator is running. When another operator is running
         * the history steps will be combined, but when one fails the history and data integrity might be
         * endangered.
         */
        try (op) {
            Pair<OpExecuteFlag, Queue<IControllerCommand>> retval = op.execute(ctx);
            final OpExecuteFlag flag = retval.getA();
            final Queue<IControllerCommand> commands = retval.getB();

            switch (flag) {
                case FINISHED -> {
                    if (this.handleWritingTransaction(type, op, ctx, ctx, commands, !this.controller.isActive())) {
                        this.cancelAll("Error in transaction of executed Operator");
                    }
                }
                case CANCELLED -> {
                    this.cancelAll("Executed Operator reported cancel.");
                }
            }
        } catch (Exception e) {
            /* UserException means the operator terminated itself -> not canceled by external systems*/
            if (!(e instanceof UserException)) op.cancel();
            this.logger.info(e);
            this.cancelAll("Executed Operator had unexpected error.");
        }
    }



    /* +----------------+
     * | INVOKE METHODS |
     * +----------------+ */



    public <P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<C> & IOTPropertyProvider<P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    public <OT extends OperatorType<C>> void invoke(OT type) {
        this._invokeOT(type, null);
    }

    public void invoke(UIOperatorType type) {
        this._invokeOT(type, null);
    }

    public <P extends OperatorPropertiesGroupStruct,
            OT extends UIOperatorType & IOTPropertyProvider<P>>
    void invoke(OT type, P props) {
        this._invokeOT(type, props);
    }

    /**
     * @return true if the operator invoke method was called
     */
    private <CTX extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX> & IOTPropertyProvider<P>,
            OT extends OperatorType<CTX>>
    boolean _invokeOT(OT type, @Nullable P props) {
        return this._invokeOT(type, props);
    }

    /**
     * @return true if the operator invoke method was called
     */
    private <CTX extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX> & IOTPropertyProvider<P>,
            OT extends OperatorType<CTX>>
    boolean _invokeOT(OT type, @Nullable P props, @Nullable HotKeyConfigGroupStruct invokedConfig) {
        if (this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }
        this.isExecuting = true;
        try {
            try {
                if (!this.poll(type)) return false;
            } catch (UserException e) {
                e.printStackTrace();
            }

            return this._invoke(type, props == null ? this.factory.getOperator(type) : this.factory.getOperator((OTP) type, props), invokedConfig);
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
    private <CTX extends IContext, P extends OperatorPropertiesGroupStruct,
            OTP extends OperatorType<CTX> & IOTPropertyProvider<P>,
            OP extends IPropertyOperator<CTX, P>>
    boolean _invoke(OperatorType<CTX> type, @Nullable IOperator<CTX> op, @Nullable HotKeyConfigGroupStruct invokedConfig) {
        if (op == null || this.modal != null ||
                (this.runningInUIMode != null && !(type instanceof UIOperatorType))) {
            return false;
        }

        final CTX currentCTX;
        if ((currentCTX = this.getCurrentContextForType(type)) == null) {
            this.logger.info("No context available.");
            return false;
        }

        /*
         * Invoking an operator while another is running only happens when
         * an operator is running in UI mode and user tries to interact with UI.
         * In this case cancel or failure of operator should not cancel the UI mode operator.
         */
        try {
            final Pair<OpInvokeFlag, Queue<IControllerCommand>> retval = op.invoke(currentCTX);
            final OpInvokeFlag flag = retval.getA();
            final Queue<IControllerCommand> commands = retval.getB();

            switch (flag) {
                case CANCELLED -> {
                    op.close();
                    break;
                }
                case FINISHED, MODAL_RUNNING -> {
                    boolean runAsModal = flag == OpInvokeFlag.MODAL_RUNNING && op instanceof IModalOperator<CTX>;
                    if (runAsModal) {
                        /* originalProps the hotkeyconfig since it could change while we are performing the modal */
                        HotKeyConfigGroupStruct hotKeyConfig = invokedConfig != null ? this.copyHotKeyConfig(invokedConfig) : null;
                        OperatorPropertiesGroupStruct propsCopy = op instanceof IPropertyOperator<?,?> pOp ? this.copyProps(pOp.props()) : null;

                        this.modal = new RunningModal<>(type, (IModalOperator<CTX>) op, currentCTX, hotKeyConfig, propsCopy);
                        if (hotKeyConfig != null && hotKeyConfig.hasReleaseEvent()) {
                            this.blockedModalEvent = hotKeyConfig;
                        }
                    }

                    if (flag == OpInvokeFlag.MODAL_RUNNING) {
                        this.controller.startTransaction();
                    }

                    if (commands != null && !commands.isEmpty()) {
                        this.handleWritingTransaction(type, op, currentCTX, currentCTX, commands, flag == OpInvokeFlag.FINISHED);
                    }

                    if (!runAsModal) op.close();
                }
                case RUN_IN_UI -> {
                    /*
                     * atm running a UIOperatorType in ui mode makes no sense
                     * as a UIOperator might be invoked while one is already in UI mode...
                     */
                    if (!(type instanceof UIOperatorType) && type instanceof IOTPropertyProvider<?> otp
                            && op instanceof IPropertyOperator<CTX, ?> propOp) {
                        //TODO group the steps to so when the ui is closed they become one undo step
                        if (commands != null) {
                            this.handleWritingTransaction(type, op, currentCTX, currentCTX, commands, false);
                        }

                        this._uimode((OTP) otp, (OP) propOp, currentCTX);
                    }
                }
            }
        } catch (Exception e) {
            this.logger.info(e);
            e.printStackTrace();
            if (!(e instanceof UserException)) {
                op.cancel();
                op.close();
            }
        }

        return true;
    }








    private <CTX extends IContext, P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX> & IOTPropertyProvider<P>>
    void _uimode(OT type, IPropertyOperator<CTX, P> op, CTX currentContext) {
        if (this.runningInUIMode != null) return;

        //TODO possible flaw: current hovered window and app context data might not align
        if (!this.uiManager.getActiveWindow().canOpenModal()) {
            return;
        }

        final IContext currentContextCopy = currentContext.copy();
        if (!this.contextTypeSafety(type, currentContextCopy)) {
            throw new ContractViolatedException("originalProps method not correct.");
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
                    OperatorManager.this.cancelAll(null);
                    busManager.unsubscribe(this);
                    type.removeRuntimeProps(op.props());
                }
            }
        };

        type.addRuntimeProps(op.props());
        try {
            op.props().getEventBus().subscribe(subscriber);
            this.runningInUIMode = new RunningUIMode<>(type, op, (CTX) currentContextCopy, subscriber);
            this.uiManager.getActiveWindow().openModalAt(ui, 0, 0);
        } catch (Exception e) {
            type.removeRuntimeProps(op.props());
            throw e;
        }
    }

    private void commitUIMode() {
        RunningUIMode uiMode = this.runningInUIMode;
        this.controller.commitTransaction(this.getTitle(uiMode.type, uiMode.op, uiMode.triggered));
        this.runningInUIMode.op.close();
        this.runningInUIMode = null;
    }

    private void cancelUIMode() {
        this.runningInUIMode.op.cancel();
        this.setUIModalNull();
    }

    private void setModalNull() {
        this.modal.op.close();
        this.modal = null;
    }

    private void setUIModalNull() {
        this.runningInUIMode.op.close();
        this.runningInUIMode = null;
    }

    private <CTX extends IContext, P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX> & IOTPropertyProvider<P>>
    boolean onUIPropertyChange(BaseStructure changed) {
        if (this.runningInUIMode == null) return false;
        if (this.isUIPropChange || this.isExecuting) {
            this.logger.debug("Do not nest OperatorManager calls. Use ControllerCommands, OperatorType switching or macros.");
            throw new UnsupportedOperationException(new UserException("Cannot nest operations. See log for further information."));
        }

        this.isUIPropChange = true;
        this.isExecuting = true;

        RunningUIMode uimode = this.runningInUIMode;
        try {
            final CTX currentCTX = this.getCurrentContextForType((OT) uimode.type);
            /* onUIPropChange() could theoretically close the ui model directly and therefore close ui mode */
            Pair<OpExecuteFlag, Queue<IControllerCommand>> retval = uimode.op.onUIPropChange(new OperatorContext<>((CTX) uimode.type, currentCTX), changed);
            if (this.runningInUIMode == null) {
                this.logger.info("UI Mode was prematurely closed in onUIPropChange() method.");
                return true;
            }

            final OpExecuteFlag flag = retval.getA();
            final Queue<IControllerCommand> commands = retval.getB();

            switch (flag) {
                case CANCELLED -> {
                    return false;
                }
                case FINISHED -> {
                    return commands == null || this.handleWritingTransaction((OT) uimode.type, uimode.op, (CTX) uimode.triggered, currentCTX, commands, false);
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
        this.isExecuting = true;

        try {
            IContext currentCTX = this.getCurrentContextForType(this.modal.type);
            IContext triggerCopy = this.modal.triggered.copy();

            if (!this.contextTypeSafety(this.modal.type, triggerCopy) ||
                    !this.contextTypeSafety(this.modal.type, currentCTX)) {
                this.modal.op.cancel();
                this.setModalNull();
                return;
            }

            OperatorContext opCtx = new OperatorContext<>(triggerCopy, currentCTX);
            final Pair<IModalOperator.Modal, Queue<IControllerCommand>> retval = this.modal.op.modal(opCtx, event);
            final OpModalFlag flag = retval.getA().flag();
            final Queue<IControllerCommand> commands = retval.getB();

            switch (flag) {
                case CANCELLED -> {
                    if (this.controller.isActive()) this.controller.abortTransaction();
                    this.setModalNull();
                }
                case FINISHED, MODAL_RUNNING -> {
                    if (commands != null &&
                            !this.handleWritingTransaction((OperatorType) this.modal.type, (IModalOperator) this.modal.op,
                                    triggerCopy, currentCTX, commands, flag == OpModalFlag.FINISHED)) {
                        this.setModalNull();
                        break;
                    }

                    if (flag == OpModalFlag.FINISHED) {
                        this.setModalNull();
                    }
                }
            }
        } catch (Exception e) {
            this.controller.abortTransaction();
            /* UserException would come from the operator method itself, meaning operator is not cancelled by external systems */
            if (!(e instanceof UserException)) {
                modal.op.cancel(); //TODO do not cancel in try statement -> cancel exception would lead to two cancel calls.
            }
            this.setModalNull();
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
        IContext context = this.getCurrentContextForType(this.modal.type);
        OperatorContext ctx = new OperatorContext<>(this.modal.triggered.copy(), this.getCurrentContextForType(this.modal.type).copy());
        try {
            Pair<OpExecuteFlag, Queue<IControllerCommand>> ret = this.modal.op.confirmModal(ctx, event);
            if (ret.getA() == OpExecuteFlag.CANCELLED) {
                this.controller.abortTransaction();
                return;
            }

            if (ret.getB() != null) {
                this.handleWritingTransaction((OperatorType) this.modal.type, (IModalOperator) this.modal.op,
                        this.modal.triggered, context, ret.getB(), true);
            } else {
                this.handleCommitTransaction((OperatorType) this.modal.type, (IModalOperator) this.modal.op,
                        this.modal.triggered, context);
            }
        } catch (UserException e) {

        } finally {
            this.setModalNull();
        }
    }

    @NotNull
    private <CTX extends IContext> String getTitle(OperatorType<CTX> type, IOperator<CTX> op, CTX triggered) {
        final CTX context;
        if ((context = this.getCurrentContextForType(type)) == null || !(op instanceof ITitleProvider<CTX> titleProvider)) {
            return type.title;
        }
        IContext triggeredCopy = triggered.copy();
        if (!this.contextTypeSafety(type, triggeredCopy)) {
            throw new ContractViolatedException("The originalProps() method of the context did not provide the correct type.");
        }

        OperatorContext<CTX> ctx = new OperatorContext<>((CTX) triggeredCopy, context);
        String title = titleProvider.getTitle(ctx);
        return title == null ? type.title : title;
    }

    private <CTX extends IContext> boolean poll(OperatorType<CTX> type) throws UserException {
        final CTX ctx;
        return ((ctx = this.getCurrentContextForType(type)) != null) && type.poll(ctx);
    }

    /**
     * Cancels current running operators (UI/Modal)
     * @param messsage the message for the user why the operators stopped
     */
    private void cancelAll(@Nullable String messsage) {
        if (this.modal != null) {
            this.cancelModal();
        }

        if (this.runningInUIMode != null) {
            this.cancelUIMode();
        }

        //TODO we need to be able to detect whether the current transaction was started by operatorManager.
        // Otherwise we might accidentally abort transactions that are not ours
        if (this.controller.isActive()) {
            this.controller.abortTransaction();
        }
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
     * Calls {@link IOperator#cancel()} on error.
     * @param triggered the context the operator was triggered in, can be different from current context.
     * @return true on success.
     */
    private <CTX extends IContext> boolean handleWritingTransaction(OperatorType<CTX> type, IOperator<CTX> op,
                                                                    CTX triggered, CTX current, Queue<IControllerCommand> commands, boolean commit) {
        if (commands.isEmpty()) return true;
        if (!this.controller.isActive()) this.controller.startTransaction();

        ControllerResult result = this.controller.writeToTransaction(commands);

        if (result.state() == ControllerResult.State.ERROR) {
            if (result.exception() != null) {
                this.logger.error("An error occured: " + result.userMessage(), result.exception());
            } else {
                this.logger.error("An error occured: " + result.userMessage());
            }

            op.cancel();

            return false;
        }

        if (commit) {
            return this.handleCommitTransaction(type, op, triggered, current);
        }

        return true;
    }

    /**
     * Handles committing a transaction, if controller is active and pushing to history.<br>
     * Handles errors and UI presentation of errors.<br>
     * Calls {@link IOperator#cancel()} on error.
     * @param triggered the context the operator was triggered in. Can be different from the current context.
     * @return true if it was successful
     */
    private <CTX extends IContext> boolean handleCommitTransaction(OperatorType<CTX> type, IOperator<CTX> op, CTX triggered, CTX current) {
        if (!this.controller.isActive()) {
            return false;
        }

        ControllerResult result = this.controller.commitTransaction(this.getTitle(type, op, triggered));
        if (result.state() == ControllerResult.State.ERROR) {
            if (result.exception() != null) {
                this.logger.error("An error occured: " + result.userMessage(), result.exception());
            } else {
                this.logger.error("An error occured: " + result.userMessage());
            }

            op.cancel();

            return false;
        }

        OperatorPropertiesGroupStruct props = op instanceof IPropertyOperator<?,?> propOp ? propOp.props() : null;
        if (type.pushUndo()) this.historyManager.pushUndo(result.transaction(), type, props);

        return true;
    }

    @Nullable
    private <CTX extends IContext> CTX getCurrentContextForType(OperatorType<CTX> type) {
        final C ctx;

        if ((ctx = this.uiManager.getCurrentOperatorContext()) == null) {
            return null;
        }

        try {
            if (type instanceof UIOperatorType) {
                return (CTX) ctx.getUIContext();
            } else {
                return (CTX) this.contextClass.cast(ctx);
            }
        } catch (ClassCastException e) {
            this.logger.error(e);
            return null;
        }
    }

    /**
     * @param type
     * @param context
     * @return true if the given context is the correct type for the given {@link OperatorType}
     */
    private boolean contextTypeSafety(OperatorType type, IContext context) {
        if (context == null) return true;
        if (type instanceof UIOperatorType) {
            return context instanceof UIOperatorContext;
        } else {
            return this.contextClass.isInstance(context);
        }
    }

    private void runModalFunctions(KeyMapEvent event) {
        for (ModalFuncKeyConfigItemGroupStruct<C> func : this.config.modalKeyConfigs.modalFunctions) {
            if (func.hotKeyConfigSelector.get().onEvent(event)) {
                ModalFunctionGroupStruct modifier = func.modifier.get();
                /*
                 * ATTENTION: any states could be modified by this and need to be rechecked...
                 */
                modifier.onEvent(this, new OperatorContext(this.modal.triggered.copy(),
                        this.uiManager.getCurrentOperatorContext()), this.modal.op);
                break;
            }
        }
    }

    private void runPropertyModifiers(KeyMapEvent event) {
        if (!(this.modal.op instanceof IPropertyOperator propOp)) return;

        Iterator<Pair<HotKeyConfigGroupStruct, PropertyModifierGroupStruct>> it = this.modal.executedModifiersWaiting.iterator();
        while (it.hasNext()) {
            Pair<HotKeyConfigGroupStruct, PropertyModifierGroupStruct> pair = it.next();
            if (pair.getA().isStopEvent(event)) {
                pair.getB().onStopEvent(this.modal.originalProps.copy(), propOp.props());
                it.remove();
                return;
            }
        }

        PropModifierKeyConfigListStruct<?> propertyModalKeymap;
        if ((propertyModalKeymap = this.config.modalKeyConfigs.propModifiers.getModalKeymapForProps(propOp.props())) != null) {
            for (PropModifierKeyConfigItemGroupStruct<?> cfg : propertyModalKeymap.getChildren()) {
                if (cfg.hotKeyConfigSelector.get().onEvent(event)) {
                    PropertyModifierGroupStruct modifier = cfg.modifier.get();
                    modifier.onEvent(this.modal.originalProps.copy(), propOp.props());
                    if (cfg.hotKeyConfigSelector.get().useStopEvent()) {
                        this.modal.executedModifiersWaiting.add(new Pair<>(this.copyHotKeyConfig(cfg.hotKeyConfigSelector.get()), cfg.modifier.get()));
                    }
                    break;
                }
            }
        }
    }

    private HotKeyConfigGroupStruct copyHotKeyConfig(HotKeyConfigGroupStruct invokedConfig) {
        HotKeyConfigRegistry registry = this.config.hotKeyConfigRegistry;

        Integer id;
        if ((id = registry.getID(invokedConfig)) == null) {
            throw new IllegalArgumentException("The hotkey configuration is not registered.");
        }

        HotKeyConfigGroupStruct hotKeyConfig;
        if ((hotKeyConfig = registry.getNew(id)) == null) {
            throw new IllegalArgumentException("The hotkey configuration is not registered.");
        }

        hotKeyConfig.fromData(invokedConfig.toData());
        return hotKeyConfig;
    }

    private OperatorPropertiesGroupStruct copyProps(OperatorPropertiesGroupStruct props) {
        OperatorPropertiesGroupStruct propsCopy = props.copy();
        if (!propsCopy.getClass().equals(props.getClass())) {
            throw new ContractViolatedException("The original property copy() method of the OperatorProperties is not implemented correctly.");
        }

        return propsCopy;
    }

    @Override
    public boolean onEvent(KeyMapEvent event) {
        if (this.blockedModalEvent != null && this.blockedModalEvent.isReleaseEvent(event)) {
            this.blockedModalEvent = null;
        }

        //TODO check Blender events -> pressing key is not the same as pressing key while moving, maybe only trigger one type of event
        // or not, the keymap events are different than the hotkey trigger logic, think whether this generally allows two similar events to work like that
        // only with hotkey logic.
        //TODO operator type switching and Operator needs to receive previous properties so the previous operator could influence the next
        /*
         * Check if modal operator should be cancelled or committed.
         */
        if (this.modal != null) {
            boolean isStopEvent = this.modal.hotkey != null && this.modal.hotkey.useStopEvent() && this.modal.hotkey.isStopEvent(event);

            if (isStopEvent) {
                this.commitModal(event);
                return true;
            }

            /* no need to return true for executing modal functions/ property modifiers,
            * as later the modal itself will return true for this event. */
            try {
                this.runModalFunctions(event);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancelModal();
                return true;
            }

            if (this.modal == null) return true;

            try {
                this.runPropertyModifiers(event);
            } catch (Exception e) {
                e.printStackTrace();
                this.cancelModal();
                return true;
            }
        }

        /*
         * No Modal operator running, try and trigger?
         */
        boolean isEventBlocked = this.blockedModalEvent != null && this.blockedModalEvent.onEvent(event);
        if (this.modal == null && !isEventBlocked) {
            // TODO Mittlere Maus ohne shift vs mittlere maus mit shift, welcher soll getriggered werden? Muss in hotkeyconfig implementiert werden
            Set<OperatorKeyConfigItemGroupStruct<C>> configs = this.config.getConfigs(event);
            for (OperatorKeyConfigItemGroupStruct<C> config : configs) {
                OperatorType<C> ot;
                if ((ot = config.operatorType.getReference()) == null) continue;

                if (ot instanceof IOTPropertyProvider provider) {
                    OperatorPropertiesGroupStruct newProps = provider.createProps();
                    newProps.copyFrom(config.props.getProps());
                    return this._invokeOT(ot, newProps, config.hotKeyConfigSelector.get());
                } else {
                    return this._invokeOT(ot, null, config.hotKeyConfigSelector.get());
                }
            }
        } else {
            this._modal(event);
            return true;
        }

        return false;
    }

    /**
     *
     * @param type
     * @param op
     * @param triggered
     * @param hotkey null if the modal operator was not triggered by a hotkey configuration.
     * @param executedModifiersWaiting set of modifiers that await a stop event.
     * @param <CTX>
     */
    protected record RunningModal<CTX extends IContext>(OperatorType<CTX> type, IModalOperator<CTX> op, CTX triggered,
                                                        @Nullable HotKeyConfigGroupStruct hotkey,
                                                        @Nullable OperatorPropertiesGroupStruct originalProps,
                                                        Set<Pair<HotKeyConfigGroupStruct, PropertyModifierGroupStruct>> executedModifiersWaiting) {
        public RunningModal(OperatorType<CTX> type, IModalOperator<CTX> op, CTX triggered, @Nullable HotKeyConfigGroupStruct hotkey, @Nullable OperatorPropertiesGroupStruct copy) {
            this(type, op, triggered, hotkey, copy, new HashSet<>());
        }
    }

    protected record RunningUIMode<CTX extends IContext,
            P extends OperatorPropertiesGroupStruct,
            OT extends OperatorType<CTX> & IOTPropertyProvider<P>>
            (OT type, IPropertyOperator<CTX, P> op, CTX triggered, Object propSubscriber) {
    }
}
