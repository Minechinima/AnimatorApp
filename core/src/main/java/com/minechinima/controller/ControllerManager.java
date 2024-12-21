package com.minechinima.controller;

import com.minechinima.controller.api.IControllerCommand;
import com.minechinima.model.DataTreeManager;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.base.BaseValueHierarchy;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;

import java.util.Queue;
import java.util.Stack;

public class ControllerManager {
    private Transaction currentTransaction;
    private final DataTreeManager<?> dataTree;

    public ControllerManager(DataTreeManager<?> app) {
        this.dataTree = app;
    }

    public boolean isActive() {
        return this.currentTransaction != null;
    }

    public DataTreeManager<?> getDataTree() {
        return this.dataTree;
    }

    /**
     * @throws UnsupportedOperationException when there is a transaction currently running
     */
    public ControllerResult executeTransaction(String name, Queue<IControllerCommand> commands) {
        this.startTransaction();
        ControllerResult r0 = this.writeToTransaction(commands);
        ControllerResult r1 = this.commitTransaction(name);

        if (r0.state() == ControllerResult.State.ERROR) {
            return r0;
        }

        if (r1.state() == ControllerResult.State.ERROR) {
            this.abortTransaction();
            return r1;
        }

        return r1;
    }

    /**
     * @throws UnsupportedOperationException when there is a transaction currently running
     */
    public void startTransaction() {
        if (this.currentTransaction != null) {
            throw new UnsupportedOperationException("Cannot start a database transaction when one is currently running.");
        }

        this.currentTransaction = new Transaction();
        this.dataTree.getRoot().getLogging().registerLog(this.currentTransaction.getLog());
    }

    /**
     * @param commands
     * @return
     * @throws UnsupportedOperationException when no transaction was started.
     */
    public ControllerResult writeToTransaction(Queue<IControllerCommand> commands) {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot write to transaction when none is started.");
        }

        this.dataTree.getReferenceManager().startCachingClearedReferences();
        this.currentTransaction.getLog().resume();

        try {
            while (!commands.isEmpty()) {
                IControllerCommand command = commands.poll();
                command.execute();
            }
        } catch (Exception e) {
            this.abortTransaction();
            this.dataTree.getReferenceManager().reapplyCachedClearedReferences();

            if (e instanceof UserException userException) {
                return new ControllerResult(ControllerResult.State.ERROR, userException.getUserMessage(), e);
            }

            return new ControllerResult(ControllerResult.State.ERROR, "An unexpected error happened.", e);
        }

        this.currentTransaction.getLog().pause();
        this.dataTree.getReferenceManager().stopCachingClearedReferences();

        return new ControllerResult(ControllerResult.State.SUCCESS, this.currentTransaction.getLocalizedTitle());
    }

    /**
     * @return
     * @throws UnsupportedOperationException when no transaction is active.
     */
    public ControllerResult commitTransaction(String name) {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot commit transaction when none is started.");
        }

        this.currentTransaction.setTitle(name);
        this.dataTree.getReferenceManager().finalizeCachedClearedReferences();
        this.currentTransaction.committedState();
        this.currentTransaction = null;
        return new ControllerResult(ControllerResult.State.SUCCESS, name);
    }

    /**
     * Revert the current active transaction.
     * @throws UnsupportedOperationException when no transaction is active.
     */
    public void abortTransaction() {
        if (this.currentTransaction == null) {
            throw new UnsupportedOperationException("Cannot commit transaction when none is started.");
        }

        this.currentTransaction.getLog().close();
        this.currentTransaction.abortedState();

        BaseValueHierarchy<?> root = this.dataTree.getRoot();
        Stack<DataOperationLog.RelativeEntry> log = this.currentTransaction.getLog().getLogRelativeTo(root);
        while (!log.isEmpty()) {
            DataOperationLog.RelativeEntry step = log.pop();
            BaseStructure context;
            IDataOperation operation = step.getOperation();

            if (step.getRelativePath() == null) {
                context = root;
            } else {
                context = root.getChildByUniquePath(step.getRelativePath());
            }

            if (context == null) {
                throw new Error("Cannot revert transaction as one element cannot be found.");
            }

            if (!operation.isCorrectType(context)) {
                throw new Error("Cannot revert transaction as the type of an element is not applicable.");
            }

            try {
                operation.undo(context);
            } catch (Exception e) {
                throw new Error(e);
            }
        }

        this.currentTransaction = null;
    }
}
