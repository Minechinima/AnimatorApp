package com.minechinima.controller;

import com.minechinima.app.IGenericApplicationData;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.logging.DataOperationLog;
import com.minechinima.model.logging.operations.IDataOperation;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.base.BaseValueHierarchy;

import java.util.Stack;

public class Controller {
    private Transaction currentTransaction;
    private final IGenericApplicationData<?> app;

    public Controller(IGenericApplicationData<?> app) {
        this.app = app;
    }

    public boolean isActive() {
        return this.currentTransaction != null;
    }

    /**
     * Starts a new transaction and commits it. Commits the last running transaction, if present.
     * Does not commit the transaction into the history of the app.
     * @param run
     * @return the newly committed transaction
     */
    public ControllerResult executeNewTransaction(String actionTitle, ControllerRunnable run) {
        if (this.currentTransaction != null) {
            this.commitCurrentTransaction();
        }
        ControllerResult result = this.startTransaction(actionTitle, run);

        if (result.state() == ControllerResult.State.SUCCESS) {
            this.commitCurrentTransaction();
        }

        return result;
    }

    /**
     * Starts a new transaction and commits it. Commits the last running transaction, if present.
     * The transaction is also committed into the history manager of the app.
     * @param run
     * @return
     */
    public ControllerResult executeNewTransaction(HistoryManager historyManager, String actionTitle, ControllerRunnable run) {
        if (this.currentTransaction != null) {
            this.commitCurrentTransaction(historyManager);
        }
        ControllerResult result = this.startTransaction(actionTitle, run);

        if (result.state() == ControllerResult.State.SUCCESS) {
            this.commitCurrentTransaction(historyManager);
        }

        return result;
    }

    /**
     * @param run
     * @return
     */
    public ControllerResult startTransaction(String actionTitle, ControllerRunnable run) {
        if (this.currentTransaction != null) {
            return new ControllerResult(ControllerResult.State.ERROR, "Cannot start a new action while there is currently one running.");
        }

        this.currentTransaction = new Transaction(actionTitle);
        this.app.getDataTree().getRoot().getLogging().registerLog(this.currentTransaction.getLog());
        return this.continueTransaction(run);
    }

    /**
     * @param run
     * @return
     */
    public ControllerResult continueTransaction(ControllerRunnable run) {
        if (this.currentTransaction == null) {
            return new ControllerResult(ControllerResult.State.ERROR, "No current running action to continue.");
        }

        this.app.getDataTree().getReferenceManager().cacheClearedReferences();
        this.currentTransaction.getLog().resume();

        try {
            run.run();
        } catch (Exception e) {
            this.currentTransaction.getLog().close();
            this.currentTransaction.abortedState();
            Transaction transaction = this.currentTransaction;
            this.currentTransaction = null;

            try {
                this.revertTransaction(transaction);
            } catch (Exception e2) {
                /* crash the app as we cannot guarantee data sync / integrity anymore */
                throw new Error("An unexpected exception happened during reverting a failed transaction.");
            }

            this.app.getDataTree().getReferenceManager().reapplyCachedClearedReferences();

            if (e instanceof UserException userException) {
                return new ControllerResult(ControllerResult.State.ERROR, userException.getUserMessage(), e);
            }

            return new ControllerResult(ControllerResult.State.ERROR, "An unexpected error happened.", e);
        }

        this.currentTransaction.getLog().pause();

        return new ControllerResult(ControllerResult.State.SUCCESS, this.currentTransaction.getLocalizedTitle());
    }

    /**
     * Commits current transaction to the main database but without committing it into the history manager of the app.
     */
    public void commitCurrentTransaction() {
        if (this.currentTransaction == null) return;
        this.app.getDataTree().getReferenceManager().finalizeCachedClearedReferences();
        this.currentTransaction.committedState();
        this.currentTransaction = null;
    }

    /**
     * Commits current transaction and pushes it into the history manager of the app
     */
    public void commitCurrentTransaction(HistoryManager historyManager) {
        if (this.currentTransaction == null) return;
        this.app.getDataTree().getReferenceManager().finalizeCachedClearedReferences();
        this.currentTransaction.committedState();
        if (historyManager != null) {
            historyManager.pushTransaction(this.currentTransaction);
        }
        this.currentTransaction = null;
    }

    /**
     *
     * @param transaction
     * @throws RuntimeException probably best to let the app crash when reverting a transaction fails for some reason.
     */
    protected void revertTransaction(Transaction transaction) {
        BaseValueHierarchy<?> root = this.app.getDataTree().getRoot();
        Stack<DataOperationLog.RelativeEntry> log = transaction.getLog().getLogRelativeTo(root);
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
                throw new IllegalStateException("Cannot revert transaction as one element cannot be found.");
            }

            if (!operation.isCorrectType(context)) {
                throw new IllegalStateException("Cannot revert transaction as the type of an element is not applicable.");
            }

            try {
                operation.undo(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
