package com.minechinima.controller;

import com.minechinima.model.base.DataTreeManager;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class ControllerManager {
    /**
     * null if no transaction is currently being performed.
     */
    @Nullable
    private Transaction currentTransaction;
    private final DataTreeManager<?> dataTree;
    private final Logger logger;
    /**
     * 0 means this controller is not called.
     */
    private int nestedLevel = 0;
    /**
     * The exception that terminated a nested execution. This is cached here
     * so it can be used by all other nested calls and returned to let every stage of the call stack
     * know the reason for termination.
     */
    @Nullable
    private Exception e;
    private boolean abort;
    private final ControllerFacade api = new ControllerFacade(this);

    public ControllerManager(DataTreeManager<?> app, Logger logger) {
        this.dataTree = app;
        app.setControllerFacade(this.api);
        this.logger = logger;
    }

    /**
     * @return true when a transaction is in the cache. This does not mean that currently write operations
     * are happening. See {@link #isWriting()}
     */
    public boolean isActive() {
        return this.currentTransaction != null;
    }

    /**
     *
     * @return true when currently data operations are performed and written to a transaction.
     */
    public boolean isWriting() {
        return this.nestedLevel > 0 && this.isActive();
    }

    public DataTreeManager<?> getDataTree() {
        return this.dataTree;
    }

    /**
     * @throws UnsupportedOperationException when there is a transaction currently running
     */
    public ControllerResult executeTransaction(String name, Iterable<IControllerCommand> commands) {
        this.startTransaction();
        ControllerResult r0 = this.writeToTransaction(commands);

        if (r0.state() == ControllerResult.State.ERROR) {
            return r0;
        }

        return this.commitTransaction(name);
    }

    /**
     * @throws UnsupportedOperationException when there is a transaction currently running
     */
    public void startTransaction() {
        if (this.currentTransaction != null) {
            throw new UnsupportedOperationException("Cannot start a database transaction when one is currently running.");
        }

        this.currentTransaction = new Transaction();
        this.dataTree.getRoot().getDataOperationBus().registerLog(this.currentTransaction.getLog());
    }

    /**
     * @param commands
     * @return
     * @throws UnsupportedOperationException when no transaction was started.
     */
    public ControllerResult writeToTransaction(@NotNull Iterable<IControllerCommand> commands) {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot write to transaction when none is started.");
        }

        if (commands == null) {
            throw new IllegalArgumentException("Commands cannot be null");
        }

        this.dataTree.getReferenceManager().startCachingClearedReferences();
        this.currentTransaction.getLog().resume();
        this.nestedLevel++;

        //TODO we need to enforce that setters can only be called from controller layer - architectural enforcement
        // and only called while controller is active - authority enforcement
        // important later for sync
        try {
            /* ATTENTION: while this is running, writeToTransaction could be called again,
             * abortTransaction could set current to null */
            if (!this.abort) {
                for (IControllerCommand cmd : commands) {
                    cmd.execute();
                    if (this.abort) break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.abort = true;

            if (this.nestedLevel == 1) {
                this._abortTransaction();
            } else {
                if (this.e == null) this.e = e;
            }

            this.dataTree.getReferenceManager().reapplyCachedClearedReferences();

            if (e instanceof UserException userException) {
                return new ControllerResult(this.currentTransaction, ControllerResult.State.ERROR, userException.getUserMessage(), e);
            }

            return new ControllerResult(this.currentTransaction, ControllerResult.State.ERROR, "An unexpected error happened.", e);
        } finally {
            this.nestedLevel--;
            /* only pause the log in the root of the controller call stack */
            if (this.nestedLevel == 0) {
                if (this.currentTransaction != null) this.currentTransaction.getLog().pause();
                this.dataTree.getReferenceManager().stopCachingClearedReferences();
            }
        }

        if (this.abort) {
            if (this.nestedLevel == 0 && this.currentTransaction != null) {
                this._abortTransaction();
            }
            Exception error = this.e;
            if (this.nestedLevel == 0) {
                this.e = null;
            }

            this.dataTree.getReferenceManager().stopCachingClearedReferences();
            return new ControllerResult(this.currentTransaction, ControllerResult.State.ERROR, "", error);
        }

        return new ControllerResult(this.currentTransaction, ControllerResult.State.SUCCESS, this.currentTransaction.getLocalizedTitle());
    }

    /**
     * @return
     * @throws UnsupportedOperationException when no transaction is active.
     */
    public ControllerResult commitTransaction(String name) {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot commit transaction when none is started.");
        }

        if (this.nestedLevel > 1) {
            /* this is illegal, needs to be caught to let dev know that committing during change events is bad. */
            throw new UnsupportedOperationException("Cannot commit a transaction from within the controller commands execution loop.");
        }

        if (this.abort) {
            return new ControllerResult(this.currentTransaction, ControllerResult.State.ERROR, "Transaction is aborted.");
        }

        this.currentTransaction.setTitle(name);
        this.dataTree.getReferenceManager().finalizeCachedClearedReferences();
        this.currentTransaction.committedState();
        ControllerResult result = new ControllerResult(this.currentTransaction, ControllerResult.State.SUCCESS, name);
        this.currentTransaction = null;
        return result;
    }

    /**
     * Revert the current active transaction.
     * @throws UnsupportedOperationException when no transaction is active.
     * @throws Error when anything wrong happens during reverting the transaction.
     * This is important to catch critical bugs and let the app terminate to prevent incorrect data.
     */
    public void abortTransaction() {
        this.abort = true;

        if (this.nestedLevel == 0) {
            this._abortTransaction();
        }
    }

    protected void _abortTransaction() {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot abort transaction when none is started.");
        }

        try {
            this.currentTransaction.getLog().close();
            this.currentTransaction.abortedState();

            BaseValueHierarchy<?> root = this.dataTree.getRoot();
            Stack<DataOperationLog.RelativeEntry> log = this.currentTransaction.getLog().getLogRelativeTo(root);
            while (!log.isEmpty()) {
                DataOperationLog.RelativeEntry step = log.pop();
                BaseStructure context;
                IDataOperation operation = step.getOperation();

                if (step.getPath() == null) {
                    context = root;
                } else {
                    context = root.getChildByUniquePath(step.getPath());
                }

                if (context == null) {
                    throw new Error("Cannot revert transaction as one element cannot be found.");
                }

                if (!operation.isCorrectType(context)) {
                    throw new Error("Cannot revert transaction as the type of an element is not applicable.");
                }


                if (!operation.undo(context)) {
                    throw new Error("Could not revert the operation " + operation);
                }
            }

            this.currentTransaction = null;
            this.abort = false;
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
