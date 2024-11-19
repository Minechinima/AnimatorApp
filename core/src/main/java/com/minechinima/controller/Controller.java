package com.minechinima.controller;

import com.minechinima.app.IGenericApplication;
import com.minechinima.model.base.BaseStructure;
import com.minechinima.model.dataoperations.DataOperationLog;
import com.minechinima.model.exceptions.UserException;
import com.minechinima.model.base.BaseValueHierarchy;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

public class Controller {
    private Transaction currentTransaction;
    private final IGenericApplication app;

    public Controller(IGenericApplication app) {
        this.app = app;
    }

    @Nullable
    public Transaction getCurrentTransaction() {
        return this.currentTransaction;
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
    public ControllerResult executeNewTransactionToHistory(String actionTitle, ControllerRunnable run) {
        if (this.currentTransaction != null) {
            this.commitCurrentTransactionToHistory();
        }
        ControllerResult result = this.startTransaction(actionTitle, run);

        if (result.state() == ControllerResult.State.SUCCESS) {
            this.commitCurrentTransactionToHistory();
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
        return this.continueTransaction(run);
    }

    public ControllerResult startOrContinueTransaction(String actionTitle, ControllerRunnable run) {
        if (this.currentTransaction == null) {
            return this.startTransaction(actionTitle, run);
        }

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

        app.getDataTree().getRoot().addLogger(this.currentTransaction.getLog());

        try {
            run.run();
        } catch (Exception e) {
            this.currentTransaction.abortedState();
            Transaction transaction = this.currentTransaction;
            this.currentTransaction = null;
            app.getDataTree().getRoot().removeLogger(transaction.getLog());
            this.revertTransaction(transaction);

            if (e instanceof UserException userException) {
                return new ControllerResult(ControllerResult.State.ERROR, userException.getUserMessage(), e);
            }

            return new ControllerResult(ControllerResult.State.ERROR, "An unexpected error happened.", e);
        }

        app.getDataTree().getRoot().removeLogger(this.currentTransaction.getLog());

        return new ControllerResult(ControllerResult.State.SUCCESS, this.currentTransaction.getLocalizedTitle());
    }

    /**
     * Commits current transaction to the main database but without committing it into the history manager of the app.
     */
    public void commitCurrentTransaction() {
        if (this.currentTransaction == null) return;
        this.currentTransaction.committedState();
        this.currentTransaction = null;
    }

    /**
     * Commits current transaction and pushes it into the history manager of the app
     */
    public void commitCurrentTransactionToHistory() {
        if (this.currentTransaction == null) return;
        this.currentTransaction.committedState();
        if (app.getHistoryManager() != null) {
            app.getHistoryManager().pushTransaction(this.currentTransaction);
        }
        this.currentTransaction = null;
    }

    /**
     *
     * @param transaction
     * @throws RuntimeException probably best to let the app crash when reverting a transaction fails for some reason.
     */
    protected void revertTransaction(Transaction transaction) {
        BaseValueHierarchy<?> root = app.getDataTree().getRoot();
        Stack<DataOperationLog.RelativeEntry> log = transaction.getLog().getLogRelativeTo(root);
        while (!log.isEmpty()) {
            DataOperationLog.RelativeEntry step = log.pop();
            BaseStructure context;

            if (step.getRelativePath() == null) {
                context = root;
            } else {
                context = root.getChildByUniquePath(step.getRelativePath());
            }

            if (context == null) {
                throw new IllegalStateException("Cannot revert transaction as one element cannot be found.");
            }

            try {
                step.getOperation().undo(context);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
